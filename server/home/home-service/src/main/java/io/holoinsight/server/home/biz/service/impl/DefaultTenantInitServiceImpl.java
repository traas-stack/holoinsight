/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import io.holoinsight.server.home.biz.service.TenantInitService;

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
}
