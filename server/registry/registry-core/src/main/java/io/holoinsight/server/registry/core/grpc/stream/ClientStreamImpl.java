/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import io.holoinsight.server.common.grpc.GenericRpcCommand;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * 客户端视角的stream
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public class ClientStreamImpl implements ClientStream {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientStreamImpl.class);
  private static final AtomicIntegerFieldUpdater<ClientStreamImpl> U =
      AtomicIntegerFieldUpdater.newUpdater(ClientStreamImpl.class, "status");

  // 4个状态
  private static final int STATUS_NEW = 0;
  private static final int STATUS_HAND_SHAKE_SENT = 1;
  private static final int STATUS_HAND_SHAKE_RECVED = 2;
  private static final int STATUS_HAND_SHAKE_TIMEOUT = 3;
  private static final int STATUS_OK = 4;
  private static final int STATUS_STOPPED = 5;

  // 静态
  private final ScheduledExecutorService scheduler;
  private final Sinks.One<Void> handshakeSink = Sinks.unsafe().one();
  @Getter
  private final StreamObserver<GenericRpcCommand> reader = new Reader();
  private final HandlerRegistry handlerRegistry;
  // 杂
  @Setter
  private Executor bizExecutor;
  private ScheduledFuture<?> handshakeTimeoutFuture;
  // 状态
  private volatile int status = STATUS_NEW;

  public ClientStreamImpl(ScheduledExecutorService scheduler, HandlerRegistry handlerRegistry) {
    this.scheduler = Objects.requireNonNull(scheduler);
    this.handlerRegistry = Objects.requireNonNull(handlerRegistry);
  }

  @Override
  public void start() {
    // nothing
  }

  @Override
  public Mono<Void> handshake(int bizType, ByteString data) {
    if (!U.compareAndSet(this, STATUS_NEW, STATUS_HAND_SHAKE_SENT)) {
      throw new IllegalStateException("state error");
    }

    GenericRpcCommand cmd = RpcCmds.create(TYPE_CLIENT_HAND_SHAKE, 0, bizType, data);
    writer.send(cmd);

    handshakeTimeoutFuture = scheduler.schedule(this::onHandshakeTimeout, 10, TimeUnit.SECONDS);
    return waitHandshakeDone();
  }

  private void onHandshakeTimeout() {
    this.handshakeTimeoutFuture = null;

    if (!U.compareAndSet(this, STATUS_HAND_SHAKE_SENT, STATUS_HAND_SHAKE_TIMEOUT)) {
      return;
    }

    handshakeSink.tryEmitError(new IllegalStateException("handshake timeout"));

    LOGGER.error("handshake timeout");

    // 握手超时
    stop();
  }

  /**
   * stop 方法必须是幂等的 安全的
   */
  @Override
  public void stop() {
    // 确保对端是关闭的
    // if (writer instanceof ClientCallStreamObserver) {
    // ((ClientCallStreamObserver<GenericRpcCommand>) writer).cancel();
    // }
    // 1. 客户端发起关闭
    // 1.1 客户端用户主动关闭: [C]
    // 1.2 客户端遇到错误关闭:
    // 1.2.1 内部方法抛异常关闭 [E]
    // 1.2.2 write 抛异常关闭 [E] 一般是对端关闭了
    // 1.2.3 遇到对端异常 [E]
    // 1.2.4 遇到对端complete [C]
    // 2. 服务端发起关闭
    // 2.1 服务端用户主动关闭 [C]
    // 2.2 服务端遇到错误关闭
    // 2.2.1 内部方法抛异常关闭 [E]
    // 2.2.2 write 抛异常关闭 [E]
    // 2.2.3 遇到对端异常 [E]
    // 2.2.4 遇到对端complete [C]
    // sendError(new StatusRuntimeException(Status.CANCELLED));

    if (U.getAndSet(this, STATUS_STOPPED) == STATUS_STOPPED) {
      return;
    }

    ScheduledFuture<?> f = this.handshakeTimeoutFuture;
    if (f != null) {
      this.handshakeTimeoutFuture = null;
      f.cancel(true);
    }

    // 对于哪些正在执行的rpc请求就忽略了, 反正他们最终发不出去
  }

  private void onHandshakeCmd(GenericRpcCommand resp) {
    // TODO 这个应该转嫁给用户
    ScheduledFuture<?> f = this.handshakeTimeoutFuture;
    if (f != null) {
      this.handshakeTimeoutFuture = null;
      f.cancel(true);
    }
    LOGGER.info("客户端与服务端握手成功, 服务端的消息是 {}", resp.getData().toStringUtf8());
    handshakeSink.tryEmitEmpty();

    if (!U.compareAndSet(this, STATUS_HAND_SHAKE_RECVED, STATUS_OK)) {
      LOGGER.error("流瞬间被关闭了...");
      stop();
    }
  }

  private void onUserCmd(GenericRpcCommand cmd) {
    switch (cmd.getRpcType()) {
      case TYPE_ONEWAY: {
        OnewayHandler h = handlerRegistry.lookupOneway(cmd.getBizType());
        if (h != null) {
          maybeRunInExecutor(() -> h.handle(Cmd.of(cmd.getBizType(), cmd.getData())));
        }
        // else just ignore
      }
        break;
      // 反向请求
      case TYPE_REQ: {
        RpcHandler h = handlerRegistry.lookupRpc(cmd.getBizType());
        if (h != null) {
          maybeRunInExecutor(() -> { //
            h.handle(Cmd.of(cmd.getBizType(), cmd.getData())).subscribe(resp -> {
              writer.send(RpcCmds.resp(cmd.getReqId(), resp.type, resp.data));
            }, error -> {
              ByteString data = ByteString.copyFromUtf8("biz exception: " + error.getMessage());
              GenericRpcCommand resp = RpcCmds.resp(cmd.getReqId(), BizTypes.BIZ_ERROR, data);
              writer.send(resp);
            });
          });
        } else {
          ByteString data = ByteString.copyFromUtf8("unsupported bizType " + cmd.getBizType());
          GenericRpcCommand resp = RpcCmds.resp(cmd.getReqId(), BizTypes.UNSUPPORTED, data);
          writer.send(resp);
        }
        break;
      }
    }
  }

  private void maybeRunInExecutor(Runnable r) {
    if (bizExecutor != null) {
      try {
        bizExecutor.execute(r);
        return;
      } catch (RejectedExecutionException ex) {
        LOGGER.error("executor rejected", ex);
      }
    }
    r.run();
  }

  public Mono<Void> waitHandshakeDone() {
    return handshakeSink.asMono();
  }

  public void setWriter(StreamObserver<GenericRpcCommand> writer) {
    this.writer.setWriter(writer);
  }

  private class Reader implements StreamObserver<GenericRpcCommand> {
    private boolean needFirst = true;

    @Override
    public void onNext(GenericRpcCommand resp) {
      try {
        if (needFirst) {
          needFirst = false;
          if (U.compareAndSet(ClientStreamImpl.this, STATUS_HAND_SHAKE_SENT,
              STATUS_HAND_SHAKE_RECVED)) {
            onHandshakeCmd(resp);
          }
          return;
        }
        onUserCmd(resp);
      } catch (Throwable e) {
        LOGGER.error("cmd handle error", e);
        writer.sendError(new StatusRuntimeException(Status.INTERNAL));
        stop();
      }
    }

    @Override
    public void onError(Throwable t) {
      // 其实此时是不是该流已经中断了?
      // TODO 有必要给对端 sendError 吗?
      // 1.2.3 遇到对端异常 [E]
      LOGGER.error("server stream error, client side will close right now", t);
      writer.sendError(new StatusRuntimeException(Status.ABORTED));
      stop();
    }

    @Override
    public void onCompleted() {
      // 1.2.4 遇到对端complete [C]
      LOGGER.error("server stream completed, client side will close right now as well");
      writer.sendComplete();
      stop();
    }
  }

  private final SafeWriter writer = new SafeWriter(new Thread.UncaughtExceptionHandler() {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      LOGGER.error("error when send cmd", e);
      writer.sendError(new StatusRuntimeException(Status.ABORTED));
      stop();
    }
  });


}
