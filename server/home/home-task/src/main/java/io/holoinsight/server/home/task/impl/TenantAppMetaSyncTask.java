/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.home.biz.service.MetaService;
import io.holoinsight.server.home.biz.service.MetaService.AppModel;
import io.holoinsight.server.home.biz.service.TenantOpsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.task.AbstractMonitorTask;
import io.holoinsight.server.home.task.MonitorTaskJob;
import io.holoinsight.server.home.common.model.TaskEnum;
import io.holoinsight.server.home.task.TaskHandler;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantAppMetaSyncTask.java, v 0.1 2022年06月30日 4:51 下午 jsy1001de Exp $
 */
@Slf4j
@Service
@TaskHandler(TaskEnum.TENANT_APP_META_SYNC)
public class TenantAppMetaSyncTask extends AbstractMonitorTask {

  private static final String meta_app = "app";

  @Autowired
  private TenantOpsService tenantOpsService;

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private MetaService metaService;

  public TenantAppMetaSyncTask() {
    super(1, 2, TaskEnum.TENANT_APP_META_SYNC);
  }

  @Override
  public boolean needRun() {
    return true;
  }

  @Override
  public List<MonitorTaskJob> buildJobs(long period) throws Throwable {
    List<MonitorTaskJob> jobs = new ArrayList<>();

    jobs.add(new MonitorTaskJob() {
      @Override
      public boolean run() throws Throwable {

        syncAoAction();
        return true;
      }

      @Override
      public String id() {
        return "TenantAppMetaSyncTask";
      }
    });
    return jobs;
  }

  private void syncAoAction() {

    List<TenantOps> all = tenantOpsService.list();
    if (CollectionUtils.isEmpty(all)) {
      return;
    }

    for (TenantOps tenantOps : all) {
      String serverTableName = tenantOps.getTenant() + "_server";
      String appTableName = tenantOps.getTenant() + "_app";

      List<AppModel> fromDbServers = metaService.getAppModelFromServerTable(tenantOps.getTenant(), serverTableName);
      Debugger.print("TenantAppMetaSyncTask", "query app list from table={} size={}",
          serverTableName, fromDbServers.size());
      if (CollectionUtils.isEmpty(fromDbServers)) {
        continue;
      }

      List<AppModel> dbApps = metaService.getAppModelFromAppTable(appTableName);
      compare(appTableName, fromDbServers, dbApps);
    }
  }

  private void compare(String appTableName, List<AppModel> fromDbServers, List<AppModel> dbApps) {
    List<Map<String, Object>> rows = new ArrayList<>();
    Set<String> appDbSets = new HashSet<>();
    if (!CollectionUtils.isEmpty(dbApps)) {
      dbApps.forEach(db -> appDbSets.add(db.getApp() + "@@" + db.getWorkspace()));
    }

    fromDbServers.forEach(appModel -> {
      if (StringUtil.isBlank(appModel.getApp()) || "-".equalsIgnoreCase(appModel.getApp())) {
        return;
      }

      Map<String, Object> map = new HashMap<>();
      map.put("_modified", System.currentTimeMillis());
      map.put("_type", meta_app);
      map.put("_workspace", appModel.getWorkspace());
      map.put("app", appModel.getApp());

      Map<String, Object> labelMap = new HashMap<>();
      labelMap.put("machineType", appModel.getMachineType());
      map.put("_label", labelMap);

      rows.add(map);
      appDbSets.remove(appModel.getApp() + "@@" + appModel.getWorkspace());
    });

    dataClientService.insertOrUpdate(appTableName, rows);

    if (!CollectionUtils.isEmpty(appDbSets)) {
      List<String> uks = new ArrayList<>();
      appDbSets.forEach(db -> {
        String[] array = StringUtils.split(db, "@@");
        StringBuilder ukValue = new StringBuilder();
        for (String uk : array) {
          ukValue.append(uk);
        }
        uks.add(MD5Hash.getMD5(ukValue.toString()));
      });
      dataClientService.delete(appTableName, uks);
    }
  }
}
