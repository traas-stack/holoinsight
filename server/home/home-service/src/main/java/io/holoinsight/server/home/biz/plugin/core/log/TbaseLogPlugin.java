/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core.log;

import io.holoinsight.server.home.biz.plugin.core.LogPlugin;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import org.springframework.stereotype.Component;

/**
 * @author masaimu
 * @version 2022-11-09 17:11:00
 */
@Component
@PluginModel(name = "com.alipay.holoinsight.plugin.TbaseLogPlugin", version = "1")
public class TbaseLogPlugin extends LogPlugin {

  public TbaseLogPlugin() {

  }
}
