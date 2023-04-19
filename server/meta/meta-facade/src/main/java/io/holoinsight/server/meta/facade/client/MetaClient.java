/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.client;

import io.holoinsight.server.meta.facade.service.AgentHeartBeatService;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.meta.facade.service.impl.AgentHeartBeatServiceImpl;
import io.holoinsight.server.meta.facade.service.impl.DataClientServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaClient.java, v 0.1 2022年03月14日 10:06 上午 jinsong.yjs Exp $
 */
@Slf4j
public class MetaClient {

  private static volatile DataClientService dataClientService;
  private static volatile AgentHeartBeatService agentHeartBeatService;

  public static DataClientService getDataClientService(String domain) {
    if (dataClientService == null) {
      synchronized (MetaClient.class) {
        if (dataClientService == null) {
          log.info("new instance of dataClientService, target address={}, remote={}", null, false);
          dataClientService = DataClientServiceImpl.getInstance(domain);
        }
      }
    }
    return dataClientService;
  }


  public static AgentHeartBeatService getAgentHeartBeatService(String domain) {
    if (agentHeartBeatService == null) {
      synchronized (MetaClient.class) {
        if (agentHeartBeatService == null) {
          log.info("new instance of agentHeartBeatService, target address={}, remote={}", null,
              false);
          agentHeartBeatService = AgentHeartBeatServiceImpl.getInstance(domain);
        }
      }
    }
    return agentHeartBeatService;
  }
}
