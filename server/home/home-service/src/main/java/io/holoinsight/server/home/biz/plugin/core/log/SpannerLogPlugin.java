/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.plugin.core.log;

import io.holoinsight.server.home.biz.plugin.core.LogPlugin;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import org.springframework.stereotype.Component;

/**
 * spanner 日志采集插件
 *
 * @author masaimu
 * @version 2022-11-02 14:20:00
 */
@Component
@PluginModel(name = "com.alipay.holoinsight.plugin.SpannerLogPlugin", version = "1")
public class SpannerLogPlugin extends LogPlugin {

    public SpannerLogPlugin() {
    }
}
