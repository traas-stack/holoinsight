/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;

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


  String getTenantServerTable(String tenant);

  String getTenantAppTable(String tenant);

  Map<String, String> getTenantMetaConditions(String workspace);

  Map<String, String> getTenantWorkspaceMetaConditions(String workspace);
}
