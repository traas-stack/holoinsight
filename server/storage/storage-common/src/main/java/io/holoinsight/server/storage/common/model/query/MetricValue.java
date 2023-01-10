/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.common.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author jiwliu
 * @version : MetricValue.java, v 0.1 2022年09月29日 16:46 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricValue {
  private Map<String, String> tags;
  private Map<Long, Double> values;
}
