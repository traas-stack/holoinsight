/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import org.springframework.stereotype.Component;

/**
 *
 * @author jsy1001de Date: 2023-03-13 Time: 13:16
 */
@Component
@PluginModel(name = "io.holoinsight.server.plugin.MultiLogPlugin", version = "1")
public class MultiLogPlugin extends LogPlugin {
  public MultiLogPlugin() {}

  public void addSpmColInPluginConf(CustomPluginConf conf) {
    LogPluginUtil.addSpmCols(conf);
  }
}
