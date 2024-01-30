/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author wangsiyuan
 * @date 2022/3/10 7:50 下午
 */
@Data
public class DataResult implements Serializable {

  private String metric;

  private Map<String, String> tags;

  private Map<Long, Double> points;

  public String getKey() {
    if (StringUtils.isEmpty(metric)) {
      return StringUtils.EMPTY;
    }
    StringBuilder key = new StringBuilder(metric);
    if (!CollectionUtils.isEmpty(tags)) {
      TreeMap<String, String> sortedMap = new TreeMap<>(tags);
      for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
        key.append(entry.getKey()) //
            .append(":") //
            .append(entry.getValue()) //
            .append(",");
      }
    }
    return key.toString();
  }
}
