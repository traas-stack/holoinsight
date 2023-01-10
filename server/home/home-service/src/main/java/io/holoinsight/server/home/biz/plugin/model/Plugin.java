/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 监控插件
 *
 * @author masaimu
 * @version 2022-10-28 17:31:00
 */
public abstract class Plugin {

  public String version;
  public String name;

  public void setVersion(String version) {
    this.version = version;
  }

  public void setName(String name) {
    this.name = name;
  }

  public abstract PluginType getPluginType();


  protected void checkTenantAndName() {
    if (StringUtils.isEmpty(this.version)) {
      throw new IllegalArgumentException("plugin version can not be empty.");
    }
    if (StringUtils.isEmpty(this.name)) {
      throw new IllegalArgumentException("plugin name can not be empty.");
    }
  }
}
