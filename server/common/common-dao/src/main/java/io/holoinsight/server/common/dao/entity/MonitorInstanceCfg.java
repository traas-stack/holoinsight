/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorInstanceCfg {
  Map<String, BigDecimal> freeQuota;
  /**
   * FALSE means open, TRUE means closed
   */
  Map<String, Boolean> closed;
}
