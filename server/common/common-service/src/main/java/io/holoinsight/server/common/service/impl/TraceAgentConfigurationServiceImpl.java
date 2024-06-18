/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.TraceAgentConfiguration;
import io.holoinsight.server.common.dao.entity.dto.TraceAgentConfigurationDTO;
import io.holoinsight.server.common.dao.mapper.TraceAgentConfigurationMapper;
import io.holoinsight.server.common.service.TraceAgentConfigurationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentConfigurationServiceImpl.java, v 0.1 2022年06月21日 3:17 下午 jinsong.yjs Exp $
 */
@Service
public class TraceAgentConfigurationServiceImpl
    extends ServiceImpl<TraceAgentConfigurationMapper, TraceAgentConfiguration>
    implements TraceAgentConfigurationService {

  @Override
  public TraceAgentConfiguration get(TraceAgentConfiguration agentConfiguration) {
    QueryWrapper<TraceAgentConfiguration> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("tenant", agentConfiguration.getTenant());
    queryWrapper.eq("service", agentConfiguration.getService());
    queryWrapper.eq("type", agentConfiguration.getType());
    queryWrapper.eq("language", agentConfiguration.getLanguage());

    return getOne(queryWrapper);
  }

  @Override
  public boolean createOrUpdate(TraceAgentConfiguration agentConfiguration) {
    TraceAgentConfiguration query = get(agentConfiguration);
    agentConfiguration.setGmtModified(new Date());
    boolean result;
    if (query == null) {
      agentConfiguration.setGmtCreate(new Date());
      result = save(agentConfiguration);
    } else {
      UpdateWrapper<TraceAgentConfiguration> updateWrapper = new UpdateWrapper<>();
      updateWrapper.eq("tenant", agentConfiguration.getTenant());
      updateWrapper.eq("service", agentConfiguration.getService());
      updateWrapper.eq("type", agentConfiguration.getType());
      updateWrapper.eq("language", agentConfiguration.getLanguage());
      updateWrapper.set("value", agentConfiguration.getValue());
      updateWrapper.set("gmt_modified", new Date());
      updateWrapper.set("modifier", agentConfiguration.getModifier());

      result = update(updateWrapper);
    }

    return result;
  }


  public List<TraceAgentConfigurationDTO> getALLFromDB() {
    List<TraceAgentConfiguration> traceAgentConfigurationList = list();
    List<TraceAgentConfigurationDTO> result = new ArrayList<>(traceAgentConfigurationList.size());
    for (TraceAgentConfiguration traceAgentConfiguration : traceAgentConfigurationList) {
      Map<String, Object> configuration = J.toMap(traceAgentConfiguration.getValue());

      TraceAgentConfigurationDTO TraceAgentConfigurationDTO = new TraceAgentConfigurationDTO(
          traceAgentConfiguration.getTenant(), traceAgentConfiguration.getService(),
          traceAgentConfiguration.getType(), traceAgentConfiguration.getLanguage(), configuration,
          traceAgentConfiguration.getCreator(), traceAgentConfiguration.getModifier(),
          traceAgentConfiguration.getGmtCreate(), traceAgentConfiguration.getGmtModified());

      result.add(TraceAgentConfigurationDTO);
    }

    return result;
  }
}
