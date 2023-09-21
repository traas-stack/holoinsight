/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AgentConfigurationService;
import io.holoinsight.server.home.dal.mapper.AgentConfigurationMapper;
import io.holoinsight.server.home.dal.model.AgentConfiguration;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentConfigurationServiceImpl.java, v 0.1 2022年06月21日 3:17 下午 jinsong.yjs Exp $
 */
@Service
public class AgentConfigurationServiceImpl extends
    ServiceImpl<AgentConfigurationMapper, AgentConfiguration> implements AgentConfigurationService {

  @Override
  public AgentConfiguration get(AgentConfiguration agentConfiguration) {
    QueryWrapper<AgentConfiguration> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("tenant", agentConfiguration.getTenant());
    queryWrapper.eq("service", agentConfiguration.getService());
    queryWrapper.eq("app_id", agentConfiguration.getAppId());
    queryWrapper.eq("env_id", agentConfiguration.getEnvId());

    return getOne(queryWrapper);
  }

  @Override
  public boolean createOrUpdate(AgentConfiguration agentConfiguration) {
    AgentConfiguration query = get(agentConfiguration);
    agentConfiguration.setGmtModified(new Date());
    boolean result;
    if (query == null) {
      agentConfiguration.setGmtCreate(new Date());
      result = save(agentConfiguration);
    } else {
      UpdateWrapper<AgentConfiguration> updateWrapper = new UpdateWrapper<>();
      updateWrapper.eq("tenant", agentConfiguration.getTenant());
      updateWrapper.eq("service", agentConfiguration.getService());
      updateWrapper.eq("app_id", agentConfiguration.getAppId());
      updateWrapper.eq("env_id", agentConfiguration.getEnvId());
      updateWrapper.set("value", agentConfiguration.getValue());
      updateWrapper.set("gmt_modified", agentConfiguration.getGmtModified());

      result = update(updateWrapper);
    }

    return result;
  }
}
