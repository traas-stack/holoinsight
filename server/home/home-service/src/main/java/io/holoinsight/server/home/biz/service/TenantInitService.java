/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;

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
}
