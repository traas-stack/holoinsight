/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import io.holoinsight.server.meta.facade.client.MetaClient;
import io.holoinsight.server.meta.facade.service.AgentHeartBeatService;
import io.holoinsight.server.meta.facade.service.DataClientService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * <p>
 * created at 2022/4/11
 *
 * @author zzhb101
 */
@Configuration
public class ProdConfiguration {
  @Bean
  public DataClientService getDataClientService(
      @Value("${holoinsight.registry.meta.vip}") String vip) {
    DataClientService dcs = MetaClient.getDataClientService(vip);
    return dcs;
  }

  @Bean
  public AgentHeartBeatService agentHeartBeatService(
      @Value("${holoinsight.registry.meta.vip}") String vip) {
    return MetaClient.getAgentHeartBeatService(vip);
  }

  @Bean
  public ProdDimService prodDimService() {
    return new ProdDimService();
  }
}
