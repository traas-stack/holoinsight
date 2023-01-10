/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import org.springframework.stereotype.Component;

/**
 * wait plugin
 *
 * @author masaimu
 * @version 2022-10-31 13:54:00
 */
@Component
@PluginModel(name = "com.alipay.holoinsight.plugin.WaitPlugin", version = "1")
public class WaitPlugin extends ChainPlugin {

  public WaitPlugin() {

  }

  @Override
  public boolean input(Object input, PluginContext context) {
    return false;
  }

  @Override
  public Object output() {
    return ScheduleTimeEnum.WAIT_5_SEC;
  }

  @Override
  public void handle() throws Exception {

  }

  @Override
  public PluginType getPluginType() {
    return PluginType.scheduler;
  }
}
