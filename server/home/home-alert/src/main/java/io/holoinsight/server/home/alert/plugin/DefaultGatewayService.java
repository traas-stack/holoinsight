/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.home.alert.model.event.AlertNotifyRequest;
import io.holoinsight.server.home.biz.plugin.model.PluginContext;

/**
 * @author masaimu
 * @version 2023-03-20 13:40:00
 */
public class DefaultGatewayService extends GatewayService {
  @Override
  protected PluginContext buildNotifyContext(String traceId, AlertNotifyRequest notify) {
    return new PluginContext();
  }
}
