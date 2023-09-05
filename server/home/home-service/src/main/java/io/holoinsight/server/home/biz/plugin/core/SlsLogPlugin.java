/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.CollectType;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: SlsLogPlugin.java, Date: 2023-08-30 Time: 15:43
 */
@Component
@PluginModel(name = "io.holoinsight.server.plugin.SlsLogPlugin", version = "1")
public class SlsLogPlugin extends LogPlugin {

  @Override
  public Map<String, Object> getExecutorSelector() {
    return GaeaConvertUtil.getCenterExecutorSelector();
  }

  public CollectType getCollectType() {
    return CollectType.CENTER;
  }
}
