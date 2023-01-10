/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import io.holoinsight.server.common.threadpool.CommonThreadPools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.holoinsight.server.registry.core.grpc.GrpcForInternal;
import io.holoinsight.server.registry.core.grpc.RegistryServiceForInternalImpl;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
@Configuration
public class ClusterConfiguration {
  @Bean
  public HandlerRegistry internalHandlerRegistry() {
    return new HandlerRegistry();
  }

  @Bean
  @GrpcForInternal
  public RegistryServiceForInternalImpl registryServiceForInternalImpl(HandlerRegistry registry) {
    return new RegistryServiceForInternalImpl(registry);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public DefaultCluster defaultCluster(CommonThreadPools commonThreadPools,
      RegistryServiceForInternalImpl service) {
    // TODO 集群内部通信使用专门的线程池吧...
    return new DefaultCluster(new StaticMemberProvider(), //
        commonThreadPools.getRpcServer(), //
        commonThreadPools.getRpcClient(), //
        commonThreadPools.getScheduler(), new DefaultDynamicProvider(), service); //
  }
}
