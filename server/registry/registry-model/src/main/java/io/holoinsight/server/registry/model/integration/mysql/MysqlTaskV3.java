/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.registry.model.integration.mysql;

import io.holoinsight.server.registry.model.integration.CollectMetricConf;
import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MysqlTaskV3.java, Date: 2024-03-15 Time: 10:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MysqlTaskV3 extends LocalIntegrationTask {
  public String username;
  public String password;
  public Integer port;
  public String sql;
  public List<CollectMetricConf> metrics;
}
