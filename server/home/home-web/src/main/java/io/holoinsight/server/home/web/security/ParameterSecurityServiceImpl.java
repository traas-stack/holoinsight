/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import io.holoinsight.server.common.model.DataQueryRequest;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-09-27 11:09:00
 */
public class ParameterSecurityServiceImpl implements ParameterSecurityService {


  @Override
  public boolean checkRuleTenantAndWorkspace(String uniqueId, String tenant, String workspace) {
    return true;
  }

  @Override
  public boolean checkMetricTenantAndWorkspace(String metricTable, String tenant,
      String workspace) {
    return true;
  }

  @Override
  public boolean checkGroupTenantAndWorkspace(Long groupId, String tenant, String workspace) {
    return true;
  }

  @Override
  public String getTenantFromMetricInfo(String metricTable) {
    return StringUtils.EMPTY;
  }

  @Override
  public String getWorkspaceFromMetricInfo(String metricTable) {
    return StringUtils.EMPTY;
  }

  @Override
  public String getStorageTenantFromMetricInfo(String metricTable) {
    return StringUtils.EMPTY;
  }

  @Override
  public List<String> getDetailFilters(DataQueryRequest request) {
    return Collections.emptyList();
  }

  @Override
  public boolean checkUserTenantAndWorkspace(String uid, MonitorUser user) {
    return true;
  }

  @Override
  public boolean checkFilterTenantAndWorkspace(String metricTable,
      Map<String, List<Object>> filters, String tenant, String workspace) {
    return true;
  }

  @Override
  public boolean checkRelateId(String relateId, String relateType, String tenant,
      String workspace) {
    return true;
  }
}
