/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * { "metric": { "type": "ceresdb", "ceresdb": { "accessUser": null, // 租户名称 "accessKey": null, //
 * token "address": "address", "port": 5000 } } }
 *
 * @author xzchaoo
 * @version 1.0: TenantOpsStorage.java, v 0.1 2022年06月21日 3:08 下午 jinsong.yjs Exp $
 */
@Data
public class TenantOpsStorage {
  public StorageMetric metric;
  public TenantWorkspace workspace;

  @Data
  public static class StorageMetric {

    public String type;
    public StorageCeresDB ceresdb;
  }

  @Data
  public static class TenantWorkspace {
    public String defaultWorkspace;
    public List<WorkspaceItem> workspaces;
  }

  @Data
  public static class StorageCeresDB {
    public String accessUser; // tenant
    public String accessKey; // token
    public String address; // ceresdb address
    public String port; // ceresdb port
    public Long ttl; // 保存数据周期
  }

  @Data
  public static class WorkspaceItem {
    public String name;
    public String desc;
  }
}
