/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class UnknownValue {
  private MergeData mergeData = new MergeData();
  private Map<String, Integer> ipCountMap = new HashMap<>();

  public void addIpCount(String hostname, int count) {
    if (StringUtils.isEmpty(hostname)) {
      return;
    }
    ipCountMap.put(hostname, ipCountMap.getOrDefault(hostname, 0) + count);
  }

  public void merge(StringBuilder reuse, AnalyzedLog al) {
    for (AnalyzedLog exist : mergeData.analyzedLogs) {
      if (exist.isSimilarTo(reuse, al)) {
        exist.merge(al);
        return;
      }
    }
    mergeData.analyzedLogs.add(al);
  }

  public void update() {
    mergeData.analyzedLogs.forEach(AnalyzedLog::update);
  }

  static class MergeData {
    List<AnalyzedLog> analyzedLogs = new ArrayList<>();
  }
}
