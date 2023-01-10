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
 * @version : AlicloudMetrics.java, v 0.1 2022年11月17日 15:53 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameMetrics {
  private String name;
  private String namespace;
  private String determineMetric;
  private List<AlicloudMetric> metrics;
}