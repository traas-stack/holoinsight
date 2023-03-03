/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.RequestType;
import io.holoinsight.server.apm.engine.storage.VirtualComponentStorage;
import io.holoinsight.server.apm.server.service.VirtualComponentService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VirtualComponentServiceImpl implements VirtualComponentService {

  @Autowired
  protected VirtualComponentStorage virtualComponentStorage;

  @Override
  public List<VirtualComponent> getDbList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<VirtualComponent> dbList = virtualComponentStorage.getComponentList(tenant, service,
        startTime, endTime, RequestType.DATABASE, Const.SOURCE);
    dbList.forEach(db -> {
      db.setType(db.getComponent());
    });

    return dbList;
  }

  @Override
  public List<VirtualComponent> getCacheList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<VirtualComponent> cacheList = virtualComponentStorage.getComponentList(tenant, service,
        startTime, endTime, RequestType.CACHE, Const.SOURCE);
    cacheList.forEach(cache -> {
      cache.setType(cache.getComponent());
    });

    return cacheList;
  }

  @Override
  public List<VirtualComponent> getMQList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<VirtualComponent> mqList = new ArrayList<>();
    mqList.addAll(virtualComponentStorage.getComponentList(tenant, service, startTime, endTime,
        RequestType.MQ, Const.SOURCE));
    mqList.addAll(virtualComponentStorage.getComponentList(tenant, service, startTime, endTime,
        RequestType.MQ, Const.DEST));
    mqList.forEach(mq -> {
      mq.setType(mq.getComponent());
    });

    return mqList;
  }

  @Override
  public List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime) throws IOException {
    return virtualComponentStorage.getTraceIds(tenant, service, address, startTime, endTime);
  }
}
