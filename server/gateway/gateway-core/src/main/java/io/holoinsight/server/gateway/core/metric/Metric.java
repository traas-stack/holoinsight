/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.metric;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * metric 模型
 * <p>
 * created at 2022/3/2
 *
 * @author sw1136562366
 */
@Getter
@Setter
@ToString
public class Metric {
  private String name;
  private Map<String, String> tags;
  private Map<String, Object> values;
}
