/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;

/**
 * @author masaimu
 * @version 2023-02-23 20:28:00
 */
public abstract class HostingPlugin extends Plugin {

  public abstract IntegrationPluginDTO apply(IntegrationPluginDTO integrationPlugin);

  public abstract Boolean disable(IntegrationPluginDTO integrationPlugin);

  @Override
  public PluginType getPluginType() {
    return PluginType.hosting;
  }

  public String getSimplePluginName() {
    String[] arr = name.split("\\.");
    return arr[arr.length - 1];
  }
}
