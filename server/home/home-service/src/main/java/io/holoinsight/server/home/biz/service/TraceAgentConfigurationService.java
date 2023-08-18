/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;


import io.holoinsight.server.home.dal.model.TraceAgentConfiguration;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TraceAgentConfigurationService extends IService<TraceAgentConfiguration> {

  TraceAgentConfiguration get(TraceAgentConfiguration agentConfiguration);

  boolean createOrUpdate(TraceAgentConfiguration agentConfiguration);

}
