/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.biz.plugin.AbstractHostingAlertPlugin;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import org.springframework.stereotype.Component;

/**
 * @author masaimu
 * @version 2023-03-24 16:30:00
 */
@Component
@PluginModel(name = DefaultHostingAlertPlugin.HOSTING_AI_ALERT, version = "1")
public class DefaultHostingAlertPlugin extends AbstractHostingAlertPlugin {
  public static final String HOSTING_AI_ALERT = "io.holoinsight.plugin.DefaultHostingAlertPlugin";

  @Override
  protected boolean invalidTenant(AlarmRuleDTO alarmRuleDTO,
      IntegrationPluginDTO disableIntegrationPlugin) {
    return false;
  }

  @Override
  protected String getProductType() {
    return HOSTING_AI_ALERT;
  }

  @Override
  public String getSourceType() {
    return "hosting_default";
  }

  @Override
  public Boolean disable(IntegrationPluginDTO integrationPlugin) {
    return disableAlertHosting(integrationPlugin);
  }
}
