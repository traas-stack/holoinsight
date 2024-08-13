/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.HashMap;
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
class KnownValue {
  /**
   * log sample
   */
  private String sample;
  /**
   * log count
   */
  private int count;

  private Map<String, Integer> ipCountMap = new HashMap<>();

  public void merge(String hostname, AnalyzedLog al) {
    count += al.getCount();
    if (StringUtils.isNotEmpty(hostname)) {
      ipCountMap.put(hostname, ipCountMap.getOrDefault(hostname, 0) + al.getCount());
    }
  }
}
