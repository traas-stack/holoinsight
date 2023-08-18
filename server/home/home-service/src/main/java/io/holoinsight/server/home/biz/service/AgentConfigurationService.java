/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.AgentConfiguration;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AgentConfigurationService extends IService<AgentConfiguration> {
  AgentConfiguration get(AgentConfiguration agentConfiguration);

  boolean createOrUpdate(AgentConfiguration agentConfiguration);
}
