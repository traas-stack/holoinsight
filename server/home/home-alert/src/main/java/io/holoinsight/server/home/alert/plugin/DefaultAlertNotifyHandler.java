/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.home.alert.model.event.AlertNotify;

/**
 * @author masaimu
 * @version 2023-03-20 13:30:00
 */
public class DefaultAlertNotifyHandler extends AlertNotifyHandler {
  @Override
  protected String getWorkspace(AlertNotify alertNotify) {
    return alertNotify.getWorkspace();
  }

  @Override
  protected String getTenant(AlertNotify alertNotify) {
    return alertNotify.getTenant();
  }

  @Override
  protected String getPid(AlertNotify alertNotify) {
    return alertNotify.getTenant();
  }

  @Override
  protected String getAppKey() {
    return "app";
  }
}
