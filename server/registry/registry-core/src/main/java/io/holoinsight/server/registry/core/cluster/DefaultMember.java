/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Mono;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.holoinsight.server.common.grpc.GenericData;
import io.holoinsight.server.common.grpc.GenericDataBatch;
import io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternalGrpc;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.xzchaoo.commons.basic.Ack;
import com.xzchaoo.commons.batchprocessor.BatchProcessorConfig;
import com.xzchaoo.commons.batchprocessor.DrainLoopBatchProcessor;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
public class DefaultMember implements Member {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMember.class);

  private final Endpoint endpoint;
  private final Executor executor;
  private final ScheduledExecutorService scheduler;
  private final DynamicProvider dp;
  private DrainLoopBatchProcessor<GenericData> bp;
  private ManagedChannel channel;

  public DefaultMember(Endpoint endpoint, Executor executor, ScheduledExecutorService scheduler,
      DynamicProvider dp) {
    this.endpoint = Objects.requireNonNull(endpoint);
    this.executor = Objects.requireNonNull(executor);
    this.scheduler = Objects.requireNonNull(scheduler);
    this.dp = Objects.requireNonNull(dp);
  }

  @Override
  public Endpoint endpoint() {
    return endpoint;
  }

  @Override
  public Channel channel() {
    return channel;
  }

  void oneway(GenericData msg) {
    if (!bp.tryAdd(msg)) {
      LOGGER.error("cluster member {} block {}", endpoint, bp.stat());
    }
  }

  Mono<Void> oneway(GenericDataBatch batch) {
    return Mono.create(sink -> {
      if (!isChannelHealthy()) {
        sink.success();
        return;
      }

      ListenableFuture<Empty> future = stub().sendEvents(batch);

      Futures.addCallback(future, new FutureCallback<Empty>() {
        @Override
        public void onSuccess(@Nullable Empty result) {
          sink.success();
        }

        @Override
        public void onFailure(Throwable t) {
          sink.success();
          LOGGER.error("sendEvents error {} {}", endpoint, t);
        }
      }, MoreExecutors.directExecutor());
    });
  }

  private RegistryServiceForInternalGrpc.RegistryServiceForInternalFutureStub stub() {
    return dp.customize(RegistryServiceForInternalGrpc.newFutureStub(channel));
  }

  private boolean isChannelHealthy() {
    ConnectivityState state = channel.getState(false);
    return state != ConnectivityState.TRANSIENT_FAILURE && state != ConnectivityState.SHUTDOWN;
  }

  private void sendEvents(List<GenericData> batch, Ack ack) {
    // 如果channel本身已经不健康了, 那么就直接return
    if (!isChannelHealthy()) {
      ack.ack();
      return;
    }

    GenericDataBatch pb = GenericDataBatch.newBuilder().addAllData(batch).build();

    oneway(pb).doOnTerminate(ack::ack).subscribe();
  }

  void start() {
    BatchProcessorConfig config = new BatchProcessorConfig();
    config.setName("Cluster-" + endpoint.getIp() + "-" + endpoint.getPort());
    config.setCopyBatchForFlush(false);
    bp = new DrainLoopBatchProcessor<>(config, this::sendEvents, scheduler);
    bp.start();
    channel = ManagedChannelBuilder.forAddress(endpoint.getIp(), endpoint.getPort()) //
        .executor(executor) //
        .usePlaintext() //
        .enableFullStreamDecompression() //
        .maxInboundMessageSize(Utils.MAX_MESSAGE_BYTES) //
        .build();
  }

  void stop() {
    channel.shutdownNow();
    try {
      channel.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOGGER.warn("await channel termination interrupted", e);
    }
  }
}
