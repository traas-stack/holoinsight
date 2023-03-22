/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.dal.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableService.java, v 0.1 2022年03月22日 11:37 上午 jinsong.yjs Exp $
 */
@Service
public class MetaTableService {

  private static final Map<String, List<String>> ukMaps = new HashMap<>();

  static {
    ukMaps.put("container", Arrays.asList("name", "namespace"));
    ukMaps.put("pod", Arrays.asList("name", "namespace"));
    ukMaps.put("node", Collections.singletonList("name"));
    ukMaps.put("vm", Collections.singletonList("ip"));
    ukMaps.put("node_tenant", Collections.singletonList("_uk"));
  }

  private static Cache<String, Map<String, List<String>>> tableUkCacheMaps =
      CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

  public Map<String, List<String>> getUksForCache(String tableName) {

    Map<String, List<String>> strings = tableUkCacheMaps.getIfPresent(tableName);
    if (!CollectionUtils.isEmpty(strings)) {
      return strings;
    }
    Map<String, List<String>> uks = getUks(tableName);
    tableUkCacheMaps.put(tableName, uks);
    return uks;
  }

  public Map<String, List<String>> getUks(String tableName) {

    if (tableName.endsWith("_app")) {
      Map<String, List<String>> appParam = new HashMap<>();
      appParam.put("app", Arrays.asList("app", "_workspace"));
      return appParam;
    }

    return ukMaps;
  }
}
