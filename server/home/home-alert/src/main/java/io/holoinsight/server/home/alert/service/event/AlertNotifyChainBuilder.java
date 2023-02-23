/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import io.holoinsight.server.home.alert.plugin.NotifyChain;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;

import java.util.List;

/**
 * @author masaimu
 * @version 2022-12-14 16:06:00
 */
public interface AlertNotifyChainBuilder {

  List<NotifyChain> buildNotifyChains(String traceId,
      List<IntegrationPluginDTO> integrationPlugins);
}
