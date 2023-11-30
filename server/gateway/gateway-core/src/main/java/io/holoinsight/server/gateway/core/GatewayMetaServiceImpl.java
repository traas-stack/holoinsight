/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.holoinsight.server.gateway.core.grpc.GatewayMetaService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;

/**
 * <p>
 * created at 2023/11/30
 *
 * @author xiangfeng.xzc
 */
@Service
public class GatewayMetaServiceImpl implements GatewayMetaService {
  private final Cache<Map<String, String>, List<Map<String, Object>>> cache =
      CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).build();
  @Autowired
  private DataClientService dataClientService;

  @Override
  public List<Map<String, Object>> queryByExample(String table, Map<String, String> example) {
    List<Map<String, Object>> cached = cache.getIfPresent(example);
    if (cached != null) {
      return cached;
    }
    QueryExample e = new QueryExample();
    e.getParams().putAll(example);
    List<Map<String, Object>> r = dataClientService.queryByExample(table, e);
    cache.put(example, r);
    return r;
  }
}
