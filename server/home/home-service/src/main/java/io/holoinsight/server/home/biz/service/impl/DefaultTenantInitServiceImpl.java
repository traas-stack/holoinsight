/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.query.grpc.QueryProto.QueryFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2023/2/2
 *
 * @author xzchaoo
 */
public class DefaultTenantInitServiceImpl implements TenantInitService {
  @Override
  public TenantOpsStorage.StorageMetric createStorageMetric(String tenant) {
    TenantOpsStorage.StorageMetric sm = new TenantOpsStorage.StorageMetric();
    // TODO impl
    sm.setType("unknown");
    return sm;
  }

  @Override
  public String getTenantServerTable(String tenant) {
    return tenant + "_server";
  }

  @Override
  public String getTenantAppTable(String tenant) {
    return tenant + "_app";
  }

  @Override
  public String getTraceTenant(String tenant) {
    return tenant;
  }

  @Override
  public String getTsdbTenant(String tenant) {
    return getTsdbTenant(tenant, null);
  }

  @Override
  public String getTsdbTenant(String tenant, MetricInfo metricInfo) {
    return tenant;
  }

  @Override
  public Boolean checkConditions(String tenant, String workspace, String environment, String metric,
      List<QueryFilter> filters, MonitorScope ms, MonitorUser mu) {
    return true;
  }

  @Override
  public Map<String, String> getTenantWorkspaceMetaConditions(String tenant, String workspace) {
    return new HashMap<>();
  }

  @Override
  public Map<String, String> getTenantServerWorkspaceMetaConditions(String tenant,
      String workspace) {
    return new HashMap<>();
  }

  @Override
  public List<QueryFilter> getTenantFilters(String tenant, String workspace, String environment,
      String metric) {
    return new ArrayList<>();
  }

  @Override
  public String getLogMonitorMetricTable(String tableName) {
    return tableName;
  }

  @Override
  public CloudMonitorRange getCollectMonitorRange(String table, String tenant, String workspace,
      List<String> strings, MetaLabel metaLabel) {
    return GaeaConvertUtil.convertCloudMonitorRange(table, metaLabel, strings);
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
  public List<IntegrationGeneratedDTO> getExtraGeneratedLists() {
    return new ArrayList<>();
  }

  @Override
  public Boolean checkIntegrationWorkspace(String workspace) {
    return Boolean.TRUE;
  }

  @Override
  public Boolean checkCustomPluginLogConfParams(String tenant, String workspace,
      CustomPluginDTO customPluginDTO) {
    return Boolean.TRUE;
  }

  @Override
  public List<String> getAggCompletenessTags() {
    return Collections.singletonList("app");
  }

  @Override
  public List<String> getAggDefaultGroupByTags() {
    return new ArrayList<>();
  }

}
