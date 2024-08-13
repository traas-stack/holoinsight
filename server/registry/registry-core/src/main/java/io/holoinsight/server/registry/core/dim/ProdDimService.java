/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.xzchaoo.commons.caffeine.SafeCaffeine;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.Data;

/**
 * <p>
 * created at 2022/4/15
 *
 * @author zzhb101
 */
public class ProdDimService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProdDimService.class);

  @Autowired
  private DataClientService dataClientService;

  // TODO 定期更新
  private SafeCaffeine<String, Map<String, Map<String, Object>>> queryAllCache;
  private SafeCaffeine<CacheKey, Map<String, Map<String, Object>>> queryByExampleCache;

  @PostConstruct
  public void init() {
    queryAllCache = new SafeCaffeine<>( //
        Caffeine.newBuilder() //
            .expireAfterWrite(Duration.ofSeconds(10)), //
        new SafeCaffeine.F<String, Map<String, Map<String, Object>>>() { //
          @Override
          public @Nullable Map<String, Map<String, Object>> load(@NonNull String table) {
            List<Map<String, Object>> rows = dataClientService.queryAll(table);
            Map<String, Map<String, Object>> m = Maps.newHashMapWithExpectedSize(rows.size());
            for (Map<String, Object> row : rows) {
              m.put((String) row.get("_uk"), row);
            }
            return m;
          }

          @Override
          public void onError(@NonNull String table, Throwable throwable) {
            LOGGER.error("query all error {}", table, throwable);
          }
        }, //
        10_000);

    queryByExampleCache = new SafeCaffeine<>( //
        Caffeine.newBuilder() //
            .expireAfterWrite(Duration.ofSeconds(10)), //
        new SafeCaffeine.F<CacheKey, Map<String, Map<String, Object>>>() { //
          @Override
          public @Nullable Map<String, Map<String, Object>> load(@NonNull CacheKey s) { //
            List<Map<String, Object>> rows = dataClientService.queryByExample(s.table, s.example);
            Map<String, Map<String, Object>> m = Maps.newHashMapWithExpectedSize(rows.size());
            for (Map<String, Object> row : rows) {
              m.put((String) row.get("_uk"), row);
            }
            return m;
          }

          @Override
          public void onError(@NonNull CacheKey s, Throwable throwable) {
            LOGGER.error("load error {}", s);
          }
        }, 10_000);
  }

  public Map<String, Map<String, Object>> queryAll(String table) {
    return this.queryAllCache.get(table);
  }

  public Map<String, Map<String, Object>> queryByExample(String table, QueryExample example) {
    CacheKey k = new CacheKey(table, example);
    return queryByExampleCache.get(k);
  }

  public Map<String, Object> queryByDimIdRealTime(String table, String dimId) {
    QueryExample qe = new QueryExample();
    qe.getParams().put("_uk", dimId);
    List<Map<String, Object>> rows = dataClientService.queryByExample(table, qe);
    if (rows.size() == 1) {
      return rows.get(0);
    }
    return null;
  }

  public Map<String, Object> queryByDimId(String table, String dimId, boolean useCache) {
    if (useCache) {
      Map<String, Map<String, Object>> m = queryAllCache.get(table);
      if (m != null) {
        return m.get(dimId);
      }
      return null;
    } else {
      QueryExample qe = new QueryExample();
      qe.getParams().put("_uk", dimId);
      List<Map<String, Object>> rows = dataClientService.queryByExample(table, qe);
      if (rows.size() == 1) {
        return rows.get(0);
      }
      if (rows.size() > 1) {
        throw new IllegalStateException("duplicated key " + dimId);
      }
      return null;
    }
  }

  @Data
  public static class CacheKey {
    private final String table;
    private final QueryExample example;
  }
}
