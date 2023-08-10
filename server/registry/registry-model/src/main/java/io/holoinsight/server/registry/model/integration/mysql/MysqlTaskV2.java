/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.registry.model.integration.mysql;

import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jsy1001de
 * @version 1.0: MysqlTaskV2.java, Date: 2023-08-09 Time: 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlTaskV2 extends LocalIntegrationTask {
  private Integer port;
  private String username;
  private String password;
}

