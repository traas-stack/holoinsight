/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.meta.facade.client.MetaClient;
import io.holoinsight.server.meta.facade.service.DataClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaConfiguration.java, v 0.1 2022年11月24日 下午7:53 jinsong.yjs Exp $
 */
@Configuration
public class MetaConfiguration {

  @Bean("dataClientService")
  public DataClientService getDataClientService(
      @Value("${holoinsight.meta.domain}") String domain) {
    return MetaClient.getDataClientService(domain);
  }

  @Bean("homeProdDimService")
  public MetaConfiguration prodDimService() {
    return new MetaConfiguration();
  }
}
