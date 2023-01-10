/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service.impl;

import io.holoinsight.server.meta.common.util.ConstPool;
import io.holoinsight.server.meta.facade.service.AbstractCacheInteractService;
import io.holoinsight.server.meta.facade.service.TableClientService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: TableInteractServiceImpl.java, v 0.1 2022年03月07日 5:39 下午 jinsong.yjs Exp $
 */
public class TableClientServiceImpl extends AbstractCacheInteractService
    implements TableClientService {

  private static volatile TableClientServiceImpl instance;

  public static TableClientServiceImpl getInstance(String domain) {
    if (instance == null) {
      synchronized (TableClientServiceImpl.class) {
        if (instance == null) {
          instance = new TableClientServiceImpl();
          instance.domains.add(domain);
        }
      }
    }
    return instance;
  }

  @Override
  public int getPort() {
    return ConstPool.GRPC_PORT_TABLE;
  }

  @Override
  public boolean healthCheck() {
    if (cookie == null) {
      return false;
    }
    return cookie.tableHeartBeat();
  }

  @Override
  public void createTable(String tableName) {
    pickOneCookie().createTable(tableName);
  }

  @Override
  public void deleteTable(String tableName) {
    pickOneCookie().deleteTable(tableName);
  }

  @Override
  public void createIndex(String tableName, String indexKey, Boolean asc) {
    pickOneCookie().createIndex(tableName, indexKey, asc);
  }

  @Override
  public void deleteIndex(String tableName, String indexKey) {
    pickOneCookie().deleteIndex(tableName, indexKey);
  }

  @Override
  public List<Object> getIndexInfo(String tableName) {
    return pickOneCookie().getIndexInfo(tableName);
  }
}
