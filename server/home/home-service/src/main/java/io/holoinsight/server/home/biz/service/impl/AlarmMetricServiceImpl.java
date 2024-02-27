/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlarmMetricService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.dal.mapper.AlarmMetricMapper;
import io.holoinsight.server.home.dal.model.AlarmMetric;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlarmMetricServiceImpl.java, Date: 2023-06-08 Time: 21:51
 */
@Service
public class AlarmMetricServiceImpl extends ServiceImpl<AlarmMetricMapper, AlarmMetric>
    implements AlarmMetricService {

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public AlarmMetric queryByMetric(Long ruleId, String metric, String tenant, String workspace) {

    QueryWrapper<AlarmMetric> wrapper = new QueryWrapper<>();
    wrapper.eq("deleted", 0);
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("rule_id", ruleId);
    wrapper.eq("metric_table", metric);
    wrapper.last("LIMIT 1");
    return getOne(wrapper);
  }

  @Override
  public List<AlarmMetric> queryByMetric(String metric, String tenant, String workspace) {
    QueryWrapper<AlarmMetric> wrapper = new QueryWrapper<>();
    wrapper.eq("deleted", 0);
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("metric_table", metric);
    return baseMapper.selectList(wrapper);
  }

  @Override
  public List<AlarmMetric> queryByRuleId(Long ruleId, String tenant, String workspace) {
    this.requestContextAdapter.tenantAdapt(tenant, workspace);
    QueryWrapper<AlarmMetric> wrapper = new QueryWrapper<>();
    wrapper.eq("deleted", 0);
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }

    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("rule_id", ruleId);
    return baseMapper.selectList(wrapper);
  }

  @Override
  public List<AlarmMetric> queryByMetric(List<String> metrics, String tenant, String workspace) {
    QueryWrapper<AlarmMetric> wrapper = new QueryWrapper<>();
    wrapper.eq("deleted", 0);
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.select().in("metric_table", metrics);
    return baseMapper.selectList(wrapper);
  }
}
