/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import org.springframework.stereotype.Component;

/**
 * @author jsy1001de
 * @version 1.0: LogPatternPlugin.java, Date: 2023-07-26 Time: 14:02
 */
@Component
@PluginModel(name = "io.holoinsight.server.plugin.LogPatternPlugin", version = "1")
public class LogPatternPlugin extends LogPlugin {
  public LogPatternPlugin() {}
}
