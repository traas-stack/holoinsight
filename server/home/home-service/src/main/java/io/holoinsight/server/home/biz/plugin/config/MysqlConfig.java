/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.config;

import io.holoinsight.server.registry.model.integration.mysql.MysqlConf;
import lombok.Data;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MysqlConfig.java, Date: 2023-08-09 Time: 13:59
 */
@Data
public class MysqlConfig {
  private List<MysqlConf> confs;

  private TelegrafConfig extraConfig;
}
