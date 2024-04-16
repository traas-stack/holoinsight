/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
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
  private MetricInfoService metricInfoService;

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
    MetricInfoDTO metricInfo = getMetricInfo(metricTable);
    if (metricInfo == null) {
      return false;
    }
    return StringUtils.equals(metricInfo.getWorkspace(), "-");
  }

  @Override
  public MetricInfoDTO getMetricInfo(String metricTable) {
    if (StringUtils.isEmpty(metricTable)) {
      return null;
    }
    return metricInfoService.queryByMetric(metricTable);
  }
}
