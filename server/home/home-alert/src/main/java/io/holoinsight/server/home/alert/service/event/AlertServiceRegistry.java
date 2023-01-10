/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 组件注册中心，所有按照环境区分的功能，都以组件的方式注册在这里。 这里还需要做检测，检测环境冲突、组件冲突等。
 */
@Component
public class AlertServiceRegistry {

  private List<? extends AlertHandlerExecutor> alertEventHanderList;
  private AlertNotifyChainBuilder alertNotifyChainBuilder;

  public List<? extends AlertHandlerExecutor> getAlertEventHanderList() {
    if (this.alertEventHanderList == null) {
      return Collections.emptyList();
    }
    return alertEventHanderList;
  }

  public AlertNotifyChainBuilder getAlertNotifyChainBuilder() {
    if (this.alertNotifyChainBuilder == null) {
      this.alertNotifyChainBuilder = new DefaultAlertNotifyChainBuilder();
    }
    return alertNotifyChainBuilder;
  }

  public void setAlertEventHanderList(List<? extends AlertHandlerExecutor> alertEventHanderList) {
    this.alertEventHanderList = alertEventHanderList;
  }

  public void setAlertNotifyChainBuilder(AlertNotifyChainBuilder alertNotifyChainBuilder) {
    this.alertNotifyChainBuilder = alertNotifyChainBuilder;
  }
}
