/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import io.holoinsight.server.home.biz.service.TenantInitService;

import java.util.HashMap;
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
  public Map<String, String> getTenantMetaConditions(String workspace) {
    return new HashMap<>();
  }

  @Override
  public Map<String, String> getTenantWorkspaceMetaConditions(String workspace) {
    return new HashMap<>();
  }

}
