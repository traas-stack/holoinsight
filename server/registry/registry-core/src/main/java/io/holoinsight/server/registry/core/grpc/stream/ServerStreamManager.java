/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.grpc.GenericRpcCommand;
import io.holoinsight.server.registry.core.agent.AgentService;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.grpc.streambiz.EchoHandshakeHandler;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
@Component
public class ServerStreamManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerStreamManager.class);
  private final Map<String, ServerStreamImpl> streams = new ConcurrentHashMap<>();
  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  @Autowired
  private AgentService agentService;
  @Autowired
  private AgentStorage agentStorage;

  public ServerStreamManager() {}

  public Set<String> getIds() {
    return streams.keySet();
  }

  public ServerStream createServerStream(StreamObserver<GenericRpcCommand> writer) {
    EchoHandshakeHandler handshakeHandler = new EchoHandshakeHandler();
    ServerStreamImpl c = new ServerStreamImpl(scheduler, writer, handshakeHandler);
    c.start();

    // 监听stream的终结

    c.waitHandshakeDone() //
        .doOnSuccess(ignored -> { //
          String agentId = handshakeHandler.getRequest().getAgentId();
          // long version = handshakeHandler.getRequest().getVersion();
          String key = agentId;

          if (agentStorage.get(key) == null) {
            c.getWriter().sendError(Status.UNAUTHENTICATED
                .withDescription("unknown agentId: " + key).asRuntimeException());
            c.stop();
            return;
          }

          c.setId(agentId);
          ServerStreamImpl old = streams.put(key, c);

          agentService.notifyBiStream(agentId);

          LOGGER.info("add bistream {}", key);
          c.stopMono().subscribe(null, null, () -> {
            LOGGER.info("remove bistream {}", key);
            streams.remove(key, c);
          });

          if (old != null) {
            LOGGER.error("close old ServerStreamImpl {}", key);
            old.stop();
          }
        }).subscribe();

    return c;
  }

  public ServerStream get(String agentId) {
    return streams.get(agentId);
  }
}
