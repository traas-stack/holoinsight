/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.impl;

import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.home.task.AbstractMonitorTask;
import io.holoinsight.server.home.task.MonitorTaskJob;
import io.holoinsight.server.home.common.model.TaskEnum;
import io.holoinsight.server.home.task.TaskHandler;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantAppServerUpdateTask.java, v 0.1 2022年08月24日 5:14 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
@TaskHandler(TaskEnum.TENANT_APP_SERVER_UPDATE)
public class TenantAppServerUpdateTask extends AbstractMonitorTask {

  public static long EXPIRED = 60 * 60000;

  @Autowired
  private MetaTableService metaTableService;

  @Autowired
  private DataClientService dataClientService;

  public TenantAppServerUpdateTask() {
    super(1, 2, TaskEnum.TENANT_APP_SERVER_UPDATE);
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
        return "TENANT_APP_SERVER_UPDATE";
      }
    });
    return jobs;
  }

  private void syncAoAction() {
    List<MetaTableDTO> all = metaTableService.findAll();

    if (CollectionUtils.isEmpty(all))
      return;

    for (MetaTableDTO metaTableDTO : all) {
      List<Map<String, Object>> mapList = dataClientService.queryAll(metaTableDTO.getName());
      Debugger.print("TenantAppServerUpdateTask", "qurey meta list from table={} size={}",
          metaTableDTO.name, mapList.size());
      if (CollectionUtils.isEmpty(mapList)) {
        continue;
      }

      Set<String> serverUkSets = new HashSet<>();

      for (Map<String, Object> map : mapList) {
        if (!map.containsKey("_modified")) {
          continue;
        }

        Double modified = (Double) map.get("_modified");

        if ((System.currentTimeMillis() - modified) > EXPIRED) {
          serverUkSets.add((String) map.get("_uk"));
        }
      }
      Debugger.print("TenantAppServerUpdateTask", "need delete expried list from table={} size={}",
          metaTableDTO.name, serverUkSets.size());

      // 熔断
      if ((serverUkSets.size() * 1.0 / mapList.size()) > 0.8) {
        log.warn("need delete expired list from table={} size={} mapList={}, is fusing",
            metaTableDTO.name, serverUkSets.size(), mapList.size());
        continue;
      }

      dataClientService.delete(metaTableDTO.name, new ArrayList<>(serverUkSets));
    }
  }
}
