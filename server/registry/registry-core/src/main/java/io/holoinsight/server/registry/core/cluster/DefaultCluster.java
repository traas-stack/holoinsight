/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Flux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.holoinsight.server.common.grpc.GenericData;
import io.holoinsight.server.common.grpc.GenericDataBatch;
import io.holoinsight.server.registry.core.grpc.RegistryServiceForInternalImpl;
import com.xzchaoo.commons.basic.Ack;
import com.xzchaoo.commons.batchprocessor.BatchProcessorConfig;
import com.xzchaoo.commons.batchprocessor.DrainLoopBatchProcessor;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
public class DefaultCluster implements Cluster {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCluster.class);

  private final MemberProvider memberProvider;
  private final ScheduledExecutorService scheduler;
  private final DrainLoopBatchProcessor<GenericData> bp;
  private final Executor serverExecutor;
  private final Executor clientExecutor;
  private final DynamicProvider defaultDynamicProvider;
  private final RegistryServiceForInternalImpl service;
  volatile State state = new State();
  private LocalMemberServer localMemberServer;

  public DefaultCluster(MemberProvider memberProvider, Executor serverExecutor,
      Executor clientExecutor, ScheduledExecutorService scheduler, DynamicProvider dp,
      RegistryServiceForInternalImpl service) {
    this.memberProvider = Objects.requireNonNull(memberProvider);
    this.serverExecutor = Objects.requireNonNull(serverExecutor);
    this.clientExecutor = Objects.requireNonNull(clientExecutor);
    this.scheduler = Objects.requireNonNull(scheduler);
    this.defaultDynamicProvider = Objects.requireNonNull(dp);
    this.service = Objects.requireNonNull(service);
    BatchProcessorConfig config = new BatchProcessorConfig();
    config.setName("Cluster-BP");
    config.setCopyBatchForFlush(false);
    bp = new DrainLoopBatchProcessor<>(config, this::sendEvents, scheduler);
    bp.start();
  }

  private void sendEvents(List<GenericData> ts, Ack ack) {
    GenericDataBatch batch = GenericDataBatch.newBuilder() //
        .addAllData(ts) //
        .build();

    Flux.fromIterable(state.members) //
        .flatMap(m -> m.oneway(batch), 16) //
        .doFinally(ignored -> ack.ack()) //
        .subscribe();
  }

  @Override
  public void broadcast(GenericData msg) {
    if (!bp.tryAdd(msg)) {
      LOGGER.error("cluster bp block {}", bp.stat());
    }
  }

  public void start() {
    startLocalServer();
    sync();
  }

  private void startLocalServer() {
    try {
      localMemberServer = new LocalMemberServer(Utils.DEFAULT_PORT, service, this.serverExecutor);
      localMemberServer.start();
    } catch (IOException e) {
      throw new IllegalStateException("fail to start local server", e);
    }
  }

  private void sync() {
    State oldState = state;
    Set<Endpoint> newMemberEndpoints = memberProvider.members();
    if (oldState.memberMap.keySet().equals(newMemberEndpoints)) {
      return;
    }

    State newState = new State();

    for (Endpoint old : oldState.memberMap.keySet()) {
      DefaultMember m = oldState.memberMap.get(old);
      if (!newMemberEndpoints.contains(old)) {
        // 延迟关闭 而不是立即关闭, 否则很难保证多步骤下的并发安全
        scheduler.schedule(m::stop, Utils.DELAY_CLOSE, TimeUnit.SECONDS);
      } else {
        newState.members.add(m);
        newState.memberMap.put(old, m);
      }
    }

    for (Endpoint newMember : newMemberEndpoints) {
      if (!oldState.memberMap.containsKey(newMember)) {
        DefaultMember m =
            new DefaultMember(newMember, clientExecutor, scheduler, defaultDynamicProvider);
        m.start();
        newState.members.add(m);
        newState.memberMap.put(newMember, m);
      }
    }

    // TODO print change information
    this.state = newState;
  }

  public void stop() {
    State state = this.state;
    this.state = new State();
    for (DefaultMember member : state.members) {
      member.stop();
    }
    if (localMemberServer != null) {
      localMemberServer.stop();
    }
  }

  static class State {
    List<DefaultMember> members = new ArrayList<>();
    Map<Endpoint, DefaultMember> memberMap = new HashMap<>();
  }
}
