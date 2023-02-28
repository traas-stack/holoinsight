/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.query.VirtualComponent;
import io.holoinsight.server.storage.common.model.specification.sw.RequestType;
import io.holoinsight.server.storage.engine.storage.VirtualComponentStorage;
import io.holoinsight.server.storage.server.service.VirtualComponentService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class VirtualComponentServiceImpl implements VirtualComponentService {

  @Resource
  @Qualifier("virtualComponentEsStorage")
  private VirtualComponentStorage virtualComponentEsService;

  @Override
  public List<VirtualComponent> getDbList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<VirtualComponent> dbList = virtualComponentEsService.getComponentList(tenant, service,
        startTime, endTime, RequestType.DATABASE, Const.SOURCE);
    dbList.forEach(db -> {
      db.setType(db.getComponent());
    });

    return dbList;
  }

  @Override
  public List<VirtualComponent> getCacheList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<VirtualComponent> cacheList = virtualComponentEsService.getComponentList(tenant, service,
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
    mqList.addAll(virtualComponentEsService.getComponentList(tenant, service, startTime, endTime,
        RequestType.MQ, Const.SOURCE));
    mqList.addAll(virtualComponentEsService.getComponentList(tenant, service, startTime, endTime,
        RequestType.MQ, Const.DEST));
    mqList.forEach(mq -> {
      mq.setType(mq.getComponent());
    });

    return mqList;
  }

  @Override
  public List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime) throws IOException {
    return virtualComponentEsService.getTraceIds(tenant, service, address, startTime, endTime);
  }
}
