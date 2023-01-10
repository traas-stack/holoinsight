/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model.alicloud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zzhb101
 * @version : MetricNamespaces.java, v 0.1 2022年11月17日 17:10 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Range {
  List<String> names;
  List<NameMetrics> nameMetrics;
}
