/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.measure;

import io.holoinsight.server.common.dao.entity.ApikeyDO;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorTokenData;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.dal.model.MarketplacePlugin;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-03-01 22:21:00
 */
public class ApikeyAuthority {

  MonitorAccessConfig accessConfig;

  List<ResourceType> resourceTypeList;

  List<ActionType> actionTypeList;

  Long marketPluginId;

  MarketplacePlugin marketplacePlugin;

  List<String> tenants;

  public static ApikeyAuthority build(MonitorAccessConfig monitorAccessConfig) {
    ApikeyAuthority apikeyAuthority = new ApikeyAuthority();
    apikeyAuthority.accessConfig = monitorAccessConfig;
    apikeyAuthority.resourceTypeList = Arrays.asList(ResourceType.values());
    apikeyAuthority.actionTypeList = Arrays.asList(ActionType.values());
    apikeyAuthority.marketPluginId = monitorAccessConfig.getMarketPluginId();
    return apikeyAuthority;
  }

  public void checkResourceType(ResourceType resourceType, MonitorTokenData tokenData) {
    if (CollectionUtils.isEmpty(this.resourceTypeList)) {
      throw new MonitorException(String.format("Resource type list is empty for token [%s] [%s].",
          tokenData.accessId, tokenData.accessKey));
    } else if (!this.resourceTypeList.contains(resourceType)) {
      throw new MonitorException(
          String.format("Can not find resource type [%s] authority for token [%s] [%s].",
              resourceType.code, tokenData.accessId, tokenData.accessKey));
    }
  }

  public void checkActionType(ActionType actionType, MonitorTokenData tokenData) {
    if (CollectionUtils.isEmpty(this.actionTypeList)) {
      throw new MonitorException(String.format("Action type list is empty for token [%s] [%s].",
          tokenData.accessId, tokenData.accessKey));
    } else if (!this.actionTypeList.contains(actionType)) {
      throw new MonitorException(
          String.format("Can not find action type [%s] authority for token [%s] [%s].",
              actionType.name(), tokenData.accessId, tokenData.accessKey));
    }
  }

  public void checkTenant(String resourceTenant, MonitorTokenData tokenData) {
    if (!CollectionUtils.isEmpty(this.tenants) && !this.tenants.contains(resourceTenant)) {
      throw new MonitorException(
          String.format("Can not find tenant [%s] authority for token [%s] [%s].", resourceTenant,
              tokenData.accessId, tokenData.accessKey));
    }
  }
}
