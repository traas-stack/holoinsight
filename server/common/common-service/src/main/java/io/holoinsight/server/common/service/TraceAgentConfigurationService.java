/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;


import io.holoinsight.server.common.dao.entity.TraceAgentConfiguration;
import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.dto.TraceAgentConfigurationDTO;

import java.util.List;

public interface TraceAgentConfigurationService extends IService<TraceAgentConfiguration> {

  TraceAgentConfiguration get(TraceAgentConfiguration agentConfiguration);

  boolean createOrUpdate(TraceAgentConfiguration agentConfiguration);

  List<TraceAgentConfigurationDTO> getALLFromDB();

}
