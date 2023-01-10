/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.ob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author zzhb101
 * @version 1.0: ObMetricConf.java, v 0.1 2022年11月21日 下午12:02 jinsong.yjs Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObMetricConf {
  public String name;
  public String metricType;
}
