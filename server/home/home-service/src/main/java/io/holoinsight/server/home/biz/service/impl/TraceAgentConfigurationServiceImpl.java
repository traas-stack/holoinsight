/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.TraceAgentConfigurationService;
import io.holoinsight.server.home.dal.mapper.TraceAgentConfigurationMapper;
import io.holoinsight.server.home.dal.model.TraceAgentConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

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
}
