/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.trigger.Filter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-02-17 14:50:00
 */
public class HostingAlertList {

  public List<HostingAlert> hostingAlertList;

  public List<AlarmRuleDTO> parseAlertRule(IntegrationProductDTO product,
      IntegrationPluginDTO pluginDTO, List<Filter> filters, String sourceType) {
    if (CollectionUtils.isEmpty(hostingAlertList)) {
      return Collections.emptyList();
    }
    List<AlarmRuleDTO> list = new ArrayList<>();
    for (HostingAlert hostingAlert : this.hostingAlertList) {
      AlarmRuleDTO alarmRuleDTO =
          hostingAlert.parseAlertRule(product, pluginDTO, filters, sourceType);
      if (alarmRuleDTO == null) {
        continue;
      }
      list.add(alarmRuleDTO);
    }
    return list;
  }
}
