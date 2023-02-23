/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO.DataRange;
import lombok.extern.slf4j.Slf4j;

/**
 * @author masaimu
 * @version 2023-02-26 14:05:00
 */
@Slf4j
public class HostingPluginInstance {

  public HostingPlugin hostingPlugin;
  public String version;
  public DataRange dataRange;
  public String status;
  public boolean isCloudRun;

  public boolean apply() {
    try {
      if (hostingPlugin == null) {
        return true;
      }
      IntegrationPluginDTO integrationPluginDTO = buildIntegrationPluginDTO();
      IntegrationPluginDTO result = hostingPlugin.apply(integrationPluginDTO);
      if (result == null) {
        return false;
      }
    } catch (Exception e) {
      log.error("fail to apply HostingPluginInstance for {}", e.getMessage(), e);
      return false;
    }
    return true;
  }

  private IntegrationPluginDTO buildIntegrationPluginDTO() {
    IntegrationPluginDTO integrationPluginDTO = new IntegrationPluginDTO();
    integrationPluginDTO.setProduct(hostingPlugin.getSimplePluginName());
    integrationPluginDTO.setVersion(hostingPlugin.version);
    integrationPluginDTO.setCollectRange(J.toMap(J.toJson(dataRange)));
    integrationPluginDTO.setCreator("holoinsight_hosting");
    if (isCloudRun) {
      integrationPluginDTO.setTenant("cloudrun");
    } else {
      integrationPluginDTO.setTenant(dataRange.getTenant(isCloudRun));
    }
    integrationPluginDTO.setWorkspace(dataRange.getWorkspace(isCloudRun));
    integrationPluginDTO.setJson(dataRange.get("envId") + "_" + dataRange.get("appId"));
    return integrationPluginDTO;
  }

  public boolean needApply() {
    return "needApply".equals(status);
  }

  public boolean needDisable() {
    return "needDisable".equals(status);
  }

  public boolean needUpgrade() {
    return "needUpgrade".equals(status);
  }

  public boolean disable() {
    try {
      if (hostingPlugin == null) {
        return true;
      }
      IntegrationPluginDTO integrationPluginDTO = buildIntegrationPluginDTO();
      hostingPlugin.disable(integrationPluginDTO);
    } catch (Exception e) {
      log.error("fail to diable HostingPluginInstance for {}", e.getMessage(), e);
      return false;
    }
    return true;
  }

  public void setNeedApplyStatus() {
    this.status = "needApply";
  }

  public void upgrade() {
    // TODO upgrade
  }
}
