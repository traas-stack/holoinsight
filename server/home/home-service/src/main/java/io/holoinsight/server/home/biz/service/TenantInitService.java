/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.query.grpc.QueryProto.QueryFilter;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2023/2/2
 *
 * @author xzchaoo
 */
public interface TenantInitService {
  /**
   * Create storage info for tenant
   * 
   * @param tenant
   * @return
   */
  TenantOpsStorage.StorageMetric createStorageMetric(String tenant);

  /**
   * get meta_server table for tenant
   * 
   * @param tenant
   * @return
   */
  String getTenantServerTable(String tenant);

  /**
   * get meta_app table for tenant
   * 
   * @param tenant
   * @return
   */
  String getTenantAppTable(String tenant);

  /**
   * get actual tenant for request tenant
   * 
   * @param tenant
   * @return
   */
  String getTraceTenant(String tenant);

  /**
   * get actual tsdb tenant for request tenant and metric
   * 
   * @param tenant
   * @return
   */
  String getTsdbTenant(String tenant);

  /**
   * get meta_server conditions for workspace
   * 
   * @param workspace
   * @return
   */
  Map<String, String> getTenantWorkspaceMetaConditions(String workspace);

  /**
   * add query filters by workspace
   * 
   * @param workspace
   * @return
   */
  List<QueryFilter> getTenantFilters(String workspace);

  /**
   * logmonitor metric table
   * 
   * @param tableName
   * @return
   */
  String getLogMonitorMetricTable(String tableName);


  CloudMonitorRange getCollectMonitorRange(String table, String workspace, List<String> strings,
      MetaLabel metaLabel);
}
