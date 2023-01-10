/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.dal.model.dto.IntegrationConfig;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jsy1001de
 * @version 1.0: IntegrationLogConfig.java, v 0.1 2022年11月17日 下午2:42 jinsong.yjs Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationLogConfig extends IntegrationConfig {
  public CustomPluginConf conf;
}
