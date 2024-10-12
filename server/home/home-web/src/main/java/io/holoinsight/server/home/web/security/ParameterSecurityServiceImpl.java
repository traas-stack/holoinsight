/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.common.model.DataQueryRequest;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
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

  @Override
  public boolean checkTenant(String target, String tenant) {
    return StringUtils.equals(target, tenant);
  }

  @Override
  public boolean checkSourceId(Long sourceId, String sourceType, String tenant, String workspace) {
    return true;
  }


  @Override
  public Boolean checkAgentLogPath(String logpath) {
    return Boolean.TRUE;
  }

  @Override
  public Boolean checkAgentLogPathPrefix(String logpath) {
    return Boolean.TRUE;
  }


  @Override
  public Boolean checkCookie(String tenant, String workspace, String environment) {
    return Boolean.TRUE;
  }

  @Override
  public Boolean checkTraceTags(String tenant, String workspace, List<Tag> tags) {
    return Boolean.TRUE;
  }

  @Override
  public Boolean checkTraceParams(String tenant, String workspace, Map<String, String> paramsMap) {
    return Boolean.TRUE;
  }


  @Override
  public Boolean checkCustomPluginLogConfParams(String tenant, String workspace,
      CustomPluginDTO customPluginDTO) {
    return Boolean.TRUE;
  }
}
