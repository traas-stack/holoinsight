/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.common.dao.entity.Tenant;
import io.holoinsight.server.common.dao.entity.Workspace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCacheService.java, v 0.1 2022年03月21日 8:24 下午 jinsong.yjs Exp $
 */
@Slf4j
public class SuperCacheService extends ScheduleLoadTask {
  private SuperCache sc;

  @Autowired
  private MetaDictValueService metaDictValueService;

  @Autowired
  private TenantService tenantService;

  @Autowired
  private WorkspaceService workspaceService;

  public SuperCache getSc() {
    return sc;
  }

  @Override
  public void load() throws Exception {
    ProdLog.info("[SuperCache] load start");
    SuperCache sc = new SuperCache();
    sc.metaDataDictValueMap = metaDictValueService.getMetaDictValue();
    sc.tenantMap = genTenantMaps();
    sc.tenantWorkspaceMaps = genTenantWorkspaceMaps();
    this.sc = sc;
    ProdLog.info("[SuperCache] load end");
  }

  private Map<String, Tenant> genTenantMaps() {
    Map<String, Tenant> map = new HashMap<>();
    List<Tenant> tenants = tenantService.list();

    if (CollectionUtils.isEmpty(tenants)) {
      return map;
    }

    tenants.forEach(tenant -> {
      map.put(tenant.getCode(), tenant);
    });

    return map;
  }

  private Map<String, List<String>> genTenantWorkspaceMaps() {
    Map<String, List<String>> listMap = new HashMap<>();
    List<Workspace> workspaces = workspaceService.list();

    if (CollectionUtils.isEmpty(workspaces)) {
      return listMap;
    }

    workspaces.forEach(workspace -> {
      if (!listMap.containsKey(workspace.getTenant())) {
        listMap.put(workspace.getTenant(), new ArrayList<>());
      }

      listMap.get(workspace.getTenant()).add(workspace.getName());
    });

    return listMap;
  }

  @Override
  public int periodInSeconds() {
    return 10;
  }

  @Override
  public String getTaskName() {
    return "SuperCacheService";
  }
}
