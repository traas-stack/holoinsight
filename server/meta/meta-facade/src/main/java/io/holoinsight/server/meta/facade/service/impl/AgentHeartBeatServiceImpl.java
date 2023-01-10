/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service.impl;

import io.holoinsight.server.meta.common.util.ConstPool;
import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.meta.facade.service.AbstractCacheInteractService;
import io.holoinsight.server.meta.facade.service.AgentHeartBeatService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentHeartBeatServiceImpl.java, v 0.1 2022年03月28日 2:55 下午 jinsong.yjs Exp $
 */
public class AgentHeartBeatServiceImpl extends AbstractCacheInteractService
    implements AgentHeartBeatService {

  private static volatile AgentHeartBeatServiceImpl instance;

  public static AgentHeartBeatServiceImpl getInstance(String domain) {
    if (instance == null) {
      synchronized (AgentHeartBeatServiceImpl.class) {
        if (instance == null) {
          instance = new AgentHeartBeatServiceImpl();
          instance.domains.add(domain);
        }
      }
    }
    return instance;
  }

  @Override
  public int getPort() {
    return ConstPool.GRPC_PORT_DATA;
  }

  @Override
  public boolean healthCheck() {
    if (cookie == null) {
      return false;
    }
    return cookie.dataHeartBeat();
  }

  @Override
  public void agentInsertOrUpdate(String tableName, String ip, String hostname,
      Map<String, Object> row) {
    pickOneCookie().agentInsertOrUpdate(tableName, ip, hostname, row);
  }

  @Override
  public void agentInsertOrUpdate(String tableName, MetaType type, List<Map<String, Object>> rows) {
    pickOneCookie().agentInsertOrUpdate(tableName, type, rows);
  }

  @Override
  public void agentDelete(String tableName, MetaType type, List<Map<String, Object>> rows) {
    pickOneCookie().agentDelete(tableName, type, rows);
  }
}
