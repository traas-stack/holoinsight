/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.holoinsight.server.common.grpc.GenericRpcCommand;
import io.holoinsight.server.registry.core.grpc.streambiz.EchoHandshakeHandler;

import io.holoinsight.server.registry.core.agent.AgentService;

/**
 * <p>created at 2022/3/3
 *
 * @author zzhb101
 */
@Component
public class ServerStreamManager {
    private static final Logger                        LOGGER    = LoggerFactory.getLogger(ServerStreamManager.class);
    private final        Map<String, ServerStreamImpl> streams   = new ConcurrentHashMap<>();
    private              ScheduledExecutorService      scheduler = Executors.newScheduledThreadPool(1);
    @Autowired
    private              AgentService                  agentService;

    public ServerStreamManager() {
    }

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

        c.waitHandshakeDone() //
            // .then(Mono.defer(() -> {
            //     long begin = System.currentTimeMillis();
            //     int count = 10000;
            //     return Flux.range(0, count) //
            //         .flatMap(i -> {
            //             return c.rpc(BizTypes.ECHO, ByteString.copyFromUtf8(Integer.toString(i))) //
            //                 .timeout(Duration.ofSeconds(3)) //
            //                 .doOnSuccess(resp -> {
            //                     int ret = Integer.parseInt(resp.getData().toStringUtf8());
            //                     if (!i.equals(ret)) {
            //                         LOGGER.error("出错了");
            //                     }
            //                 }).doOnError(error -> {
            //                     LOGGER.info("{} 错误", c.getId(), error);
            //                 });
            //         }, 16) //
            //         .doOnTerminate(() -> {
            //             LOGGER.info("");
            //             LOGGER.info("请求数={} 并发度={} 耗时={}", count, 16, System.currentTimeMillis() - begin);
            //         }).ignoreElements();
            // })) //
            .subscribe();

        return c;
    }

    public ServerStream get(String agentId) {
        return streams.get(agentId);
    }
}
