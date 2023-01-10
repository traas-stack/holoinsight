/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

/**
 * @author masaimu
 * @version 2022-11-02 12:20:00
 */
public abstract class ChainPlugin extends Plugin {

  public abstract boolean input(Object input, PluginContext context);

  public abstract Object output();

  public abstract void handle() throws Exception;

  public boolean unavailable() {
    return false;
  }
}
