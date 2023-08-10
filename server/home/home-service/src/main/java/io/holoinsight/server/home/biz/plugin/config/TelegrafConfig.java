/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.config;

import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
import lombok.Data;

/**
 * @author jsy1001de
 * @version 1.0: TelegrafConfig.java, Date: 2023-08-09 Time: 17:56
 */
@Data
public class TelegrafConfig extends LocalIntegrationTask {
  private CollectType collectType;
}
