/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jsy1001de
 * @version 1.0: SpringBootConfig.java, Date: 2023-08-23 Time: 10:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpringBootConfig extends BasePluginConfig {
  private String baseUrl;
  private String port;
}
