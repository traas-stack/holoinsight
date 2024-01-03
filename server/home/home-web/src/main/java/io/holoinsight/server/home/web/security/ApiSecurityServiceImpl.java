/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import io.holoinsight.server.home.facade.utils.ApiSecurityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-12-05 15:11:00
 */
public class ApiSecurityServiceImpl implements ApiSecurityService {

  @Autowired
  private MetricInfoMapper metricInfoMapper;

  @Override
  public boolean checkMetricTenantAndWorkspace(String metricTable, String tenant,
      String workspace) {
    return true;
  }

  @Override
  public boolean checkFilter(String metricTable, Map<String, List<Object>> filters, String tenant,
      String workspace) {
    return true;
  }

  @Override
  public boolean checkRuleTenantAndWorkspace(String uniqueId, String tenant, String workspace) {
    return true;
  }

  @Override
  public boolean isGlobalMetric(String metricTable) {
    if (StringUtils.isEmpty(metricTable)) {
      return false;
    }
    MetricInfo metricInfo = getMetricInfo(metricTable);
    if (metricInfo == null) {
      return false;
    }
    return StringUtils.equals(metricInfo.getWorkspace(), "-");
  }

  @Override
  public MetricInfo getMetricInfo(String metricTable) {
    if (StringUtils.isEmpty(metricTable)) {
      return null;
    }
    QueryWrapper<MetricInfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("metric_table", metricTable);
    queryWrapper.eq("deleted", 0);
    queryWrapper.last("LIMIT 1");
    return this.metricInfoMapper.selectOne(queryWrapper);
  }
}
