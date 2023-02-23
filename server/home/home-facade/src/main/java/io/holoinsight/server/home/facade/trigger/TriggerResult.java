/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author wangsiyuan
 * @date 2022/3/21 11:11 上午
 */
@Data
public class TriggerResult implements Serializable {

  private static final long serialVersionUID = -4588929311293650475L;

  private String metric;

  private String triggerContent;

  private Map<Long, Object> values = new TreeMap<>(java.util.Comparator.comparingLong(v -> v));

  private boolean hit;

  private List<CompareParam> compareParam;

  private Map<String, String> tags;

  private Double currentValue;

  private String triggerLevel;

  public static TriggerResult create() {
    TriggerResult result = new TriggerResult();
    return result;
  }

  public TriggerResult addValue(Long time, Object value) {
    this.values.put(time, value);
    return this;
  }


}
