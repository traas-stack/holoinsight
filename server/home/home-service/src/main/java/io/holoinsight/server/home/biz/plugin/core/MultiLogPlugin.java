/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import org.springframework.stereotype.Component;

/**
 *
 * @author jinsong.yjs
 * @version 1.0: MultiLogPlugin.java, v 0.1 2023年03月07日 下午3:35 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.server.plugin.MultiLogPlugin", version = "1")
public class MultiLogPlugin extends LogPlugin {
  public MultiLogPlugin() {}
}
