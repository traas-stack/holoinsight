/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service.impl;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstPool;
import io.holoinsight.server.meta.facade.service.AbstractCacheInteractService;
import io.holoinsight.server.meta.facade.service.DataClientService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DataClientServiceImpl.java, v 0.1 2022年03月08日 2:02 下午 jinsong.yjs Exp $
 */
public class DataClientServiceImpl extends AbstractCacheInteractService
    implements DataClientService {

  private static volatile DataClientServiceImpl instance;

  public static DataClientServiceImpl getInstance(String domain) {
    if (instance == null) {
      synchronized (DataClientServiceImpl.class) {
        if (instance == null) {
          instance = new DataClientServiceImpl();
          instance.domains.add(domain);
        }
      }
    }
    return instance;
  }

  @Override
  public void insertOrUpdate(String tableName, List<Map<String, Object>> rows) {
    pickOneCookie().insertOrUpdate(tableName, rows);
  }


  @Override
  public void delete(String tableName, List<String> uks) {
    pickOneCookie().delete(tableName, uks);
  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName) {
    return pickOneCookie().queryAll(tableName);
  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName, List<String> rowKeys) {
    return pickOneCookie().queryAll(tableName, rowKeys);
  }

  @Override
  public void deleteByExample(String tableName, QueryExample example) {
    pickOneCookie().deleteByExample(tableName, example);
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample example) {
    return pickOneCookie().queryByExample(tableName, example);
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample example) {
    return pickOneCookie().fuzzyByExample(tableName, example);
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
}
