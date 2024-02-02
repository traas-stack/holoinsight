/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.Data;

/**
 * <p>
 * created at 2024/1/11
 *
 * @author xiangfeng.xzc
 */
public class AggMetaServiceImpl implements AggMetaService {
  private DataClientService dataClientService;

  private Cache<CacheKey, List<Map<String, Object>>> cache = Caffeine.newBuilder() //
      .expireAfterWrite(Duration.ofMinutes(1)) //
      .softValues() //
      .build();

  public AggMetaServiceImpl(DataClientService dataClientService) {
    this.dataClientService = Objects.requireNonNull(dataClientService);
  }

  @Override
  public List<Map<String, Object>> find(String metaTable, Map<String, Object> condition) {
    CacheKey cacheKey = new CacheKey(metaTable, condition);
    List<Map<String, Object>> cached = cache.getIfPresent(cacheKey);
    if (cached != null) {
      return cached;
    }
    QueryExample example = new QueryExample();
    example.setParams(condition);
    List<Map<String, Object>> ret = dataClientService.queryByExample(metaTable, example);
    cache.put(cacheKey, ret);
    return ret;
  }

  @Data
  private static final class CacheKey {
    private final String metaTable;
    private final Map<String, Object> condition;
  }
}
