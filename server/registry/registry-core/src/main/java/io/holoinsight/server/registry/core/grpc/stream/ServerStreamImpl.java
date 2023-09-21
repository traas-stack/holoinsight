/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import io.holoinsight.server.common.grpc.GenericRpcCommand;
import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.Sinks;

/**
 * 服务端视角的一个stream
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */

public class ServerStreamImpl implements ServerStream {

  static final AtomicIntegerFieldUpdater<ServerStreamImpl> U =
      AtomicIntegerFieldUpdater.newUpdater(ServerStreamImpl.class, "status");

  static final AtomicLongFieldUpdater<ServerStreamImpl> NEXT_REQ_ID_U =
      AtomicLongFieldUpdater.newUpdater(ServerStreamImpl.class, "nextReqId");
  static final int HANDSHAKE_TIMEOUT_MILLS = 3000;
  /**
   * 是一个短暂的状态, 流刚建立
   */
  static final int STATUS_NEW = 0;
  /**
   * 是一个短暂的状态, 收到握手请求
   */
  static final int STATUS_HAND_SHAKE_RECVD = 1;
  /**
   * 握手成功, 可以正常服务了
   */
  static final int STATUS_OK = 2;
  /**
   * 流已经关闭
   */
  static final int STATUS_CLOSED = 3;
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerStreamImpl.class);
  private final ScheduledExecutorService scheduler;
  private final Map<Long, MonoSink<GenericRpcCommand>> callbacks = new ConcurrentHashMap<>();
  @Getter
  private final StreamObserver<GenericRpcCommand> reader = new Reader();
  private final HandshakeHandler handshakeHandler;
  private final Sinks.One<Void> handleShakeMono = Sinks.unsafe().one();
  private final Sinks.One<Void> stopMono = Sinks.unsafe().one();
  @Getter
  private final HandshakeContext handshakeContext = new HandshakeContext();
  @Getter
  @Setter
  private String id;
  private volatile int status = STATUS_NEW;
  private Future<?> handshakeTimeoutFuture;
  private volatile long nextReqId;

  public ServerStreamImpl(ScheduledExecutorService scheduler,
      StreamObserver<GenericRpcCommand> writer, HandshakeHandler handshakeHandler) {
    this.scheduler = scheduler;
    this.handshakeHandler = handshakeHandler;
    this.writer.setWriter(writer);
  }

  /**
   * 握手超时定时器
   */
  void onHandshakeTimeout() {
    // 竞争失败说明此事流状态发生了改变, 要么是提前被关闭, 要么是建流成功, 总是我们都没有处理的必要了
    if (!U.compareAndSet(this, STATUS_NEW, STATUS_CLOSED)) {
      this.handshakeTimeoutFuture = null;
      return;
    }

    // TODO 有没有关于该stream的标识符之类? 比如放在请求头里带过来吧, 不然真就空荡荡的
    LOGGER.warn("handshake timeout, close stream");
    StatusRuntimeException ex = Status.DEADLINE_EXCEEDED.withDescription("handshake timeout") //
        .asRuntimeException();
    writer.sendError(ex);
    stop0();
  }

  @Override
  public void start() {
    // 握手必须再指定时间内完成
    handshakeTimeoutFuture = scheduler.schedule(this::onHandshakeTimeout, HANDSHAKE_TIMEOUT_MILLS,
        TimeUnit.MILLISECONDS);
  }

  @Override
  public void stop() {
    // 该方法是用户主动关闭流, 可能要和内部原因关闭流区分开
    int old = U.getAndSet(this, STATUS_CLOSED);
    switch (old) {
      // 已经关闭了 没必要做任何事走了
      case STATUS_CLOSED:
        return;
      default:
        // 否则就是在如下状态
        // NEW RECVD OK
        break;
    }
    stop0();
  }

  private void cancelHandshakeTimeoutFuture() {
    Future<?> f = this.handshakeTimeoutFuture;
    if (f != null) {
      this.handshakeTimeoutFuture = null;
      f.cancel(true);
    }
  }

  private void stop0() {
    cancelHandshakeTimeoutFuture();

    // 清理掉所有等待中的 callbacks
    List<Long> reqIds = new ArrayList<>(callbacks.keySet());
    if (reqIds.size() > 0) {
      IllegalStateException streamClosedError = new IllegalStateException("stream closed", null);
      for (Long reqId : reqIds) {
        // 用如下方式保证 callback 的原子性
        MonoSink<GenericRpcCommand> cb = callbacks.remove(reqId);
        if (cb != null) {
          cb.error(streamClosedError);
        }
      }
    }

    stopMono.tryEmitEmpty();
  }

  /**
   * 读消息完成时调用该方法
   *
   * @param t 如果是死于 onError 则t为对应的异常, 否则为null
   */
  private void onReaderComplete(Throwable t) {
    if (U.getAndSet(this, STATUS_CLOSED) == STATUS_CLOSED) {
      return;
    }
    stop0();
  }

  public Mono<Void> stopMono() {
    return stopMono.asMono();
  }

  /**
   * 收到客户端的握手请求
   *
   * @param cmd
   */
  private void onClientHandshake(GenericRpcCommand cmd) {
    // 如果不是处于该状态表明被其他地方处理了, 这里直接return
    if (!U.compareAndSet(this, STATUS_NEW, STATUS_HAND_SHAKE_RECVD)) {
      return;
    }

    cancelHandshakeTimeoutFuture();

    // 进行握手
    handshakeHandler.handle(cmd, handshakeContext) //
        .subscribe(resp -> {
          if (!U.compareAndSet(this, STATUS_HAND_SHAKE_RECVD, STATUS_OK)) {
            handleShakeMono.tryEmitError(new IllegalStateException("stream closed"));
            return;
          }
          if (resp.getRpcType() != TYPE_SERVER_HAND_SHAKE) {
            // handshake handler error
            writer.sendError(
                Status.INTERNAL.withDescription("server handshake error").asRuntimeException());
            handleShakeMono.tryEmitError(new IllegalStateException("server handshake error"));
            stop0();
            return;
          }
          writer.send(resp);
          handleShakeMono.tryEmitEmpty();
        }, error -> {
          // 握手失败
          LOGGER.error("server handshake error", error);
          if (U.compareAndSet(this, STATUS_HAND_SHAKE_RECVD, STATUS_CLOSED)) {
            StatusRuntimeException ex = Status.INTERNAL.withDescription("server handshake error") //
                .withCause(error) //
                .asRuntimeException(); //
            writer.sendError(ex);
            handleShakeMono
                .tryEmitError(new IllegalStateException("server handshake error", error));
            stop0();
          } else {
            handleShakeMono
                .tryEmitError(new IllegalStateException("server handshake error", error));
          }
        });
  }

  @Override
  public Mono<Void> waitHandshakeDone() {
    return handleShakeMono.asMono();
  }

  /**
   * 对服务端来说这是有意义的
   *
   * @return
   */
  @Override
  public Mono<GenericRpcCommand> rpc(int bizType, ByteString data) {
    // quick check
    int status = U.get(this);
    if (status != STATUS_OK) {
      return Mono.error(new IllegalStateException("invalid stream status " + status));
    }

    return Mono.create(sink -> {
      long reqId = getNextReqId();
      GenericRpcCommand req = RpcCmds.req(reqId, bizType, data);
      callbacks.put(reqId, sink);
      sink.onCancel(() -> {
        // 用户取消了请求 但我们的请求已经扔出去了 没法保证了
        LOGGER.info("用户取消请求 {}", reqId);
        callbacks.remove(reqId);
      });
      writer.send(req);

      // 这里还需要再check一次
      // 我来解释一下这段代码的必要性
      // 虽然 stop0 清掉了所有pending callbacks 但上述代码可能发生在 stop0 之后, 意味着上述 callback 得不到调用
      // 我们在这里再次check status, 一旦 status 不为 STATUS_CLOSED, 就表示此时肯定还没发生stop0, 因为执行stop0之前会将CAS,
      // 将status换成CLOSED
      // 因此可见性保证: 我们的回调会被stop0看到, 因此最终一定会被清理
      // 一旦 status 为 STATUS_CLOSED, 那么它可能看不到我们的callback, 此时我们需要手动drain掉该callback
      int status2 = U.get(this);
      if (status2 == STATUS_CLOSED) {
        MonoSink<GenericRpcCommand> cb = callbacks.remove(reqId);
        if (cb != null) {
          cb.error(new IllegalStateException("invalid stream state " + status));
        }
      }

    });
  }

  private long getNextReqId() {
    return NEXT_REQ_ID_U.incrementAndGet(this);
  }

  void onCmd(GenericRpcCommand cmd) {
    // 预期只会有 TYPE_RESP
    switch (cmd.getRpcType()) {
      case TYPE_RESP:
        MonoSink<GenericRpcCommand> cb = callbacks.remove(cmd.getReqId());
        if (cb != null) {
          cb.success(cmd);
        } else {
          LOGGER.warn("callback removed {}", cmd.getReqId());
          // warn
        }
        break;
      default:
        LOGGER.warn("服务端收到未知消息 {}", cmd);
        break;
    }
  }

  @Override
  public void oneway(int bizType, ByteString data) {
    GenericRpcCommand cmd = RpcCmds.oneway(bizType, data);
    writer.send(cmd);
  }

  private void onUnexpectedError(Throwable e) {
    LOGGER.error("uncaughtException", e);
    writer.sendError(Status.INTERNAL.withCause(e).asRuntimeException());
  }

  private class Reader implements StreamObserver<GenericRpcCommand> {
    @Override
    public void onNext(GenericRpcCommand cmd) {
      switch (status) {
        case STATUS_NEW:
          onClientHandshake(cmd);
          break;
        case STATUS_OK:
          onCmd(cmd);
          break;
        case STATUS_HAND_SHAKE_RECVD:
          // handshake 完成之前就收到奇怪的消息
          StatusRuntimeException ex = Status.INTERNAL.withDescription("handshake error") //
              .asRuntimeException();
          writer.sendError(ex);
          // abort
          break;
        case STATUS_CLOSED:
          // 已经为closed状态还收到西消息 丢弃即可
        default:
          break;
      }
    }

    @Override
    public void onError(Throwable t) {
      LOGGER.info("{} 收到了对端的error ", id);
      // TODO 此时我们还能写数据过去吗? 我的意思是我还有必要要用onError给对端send一个error过去吗?
      writer.sendError(new StatusRuntimeException(Status.ABORTED));
      onReaderComplete(t);
    }

    @Override
    public void onCompleted() {
      LOGGER.info("{} 收到了对端的complete", id);
      // 由于流是双向的, 此时我肯定也要给对端发一个关闭
      writer.sendComplete();
      onReaderComplete(null);
    }
  }

  @Getter
  private final SafeWriter writer = new SafeWriter((t, e) -> onUnexpectedError(e));
}
