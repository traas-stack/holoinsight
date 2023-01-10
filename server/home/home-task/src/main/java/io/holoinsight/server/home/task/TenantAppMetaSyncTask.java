/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.task;

import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantAppMetaSyncTask.java, v 0.1 2022年06月30日 4:51 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
@TaskHandler(TaskEnum.TENANT_APP_META_SYNC)
public class TenantAppMetaSyncTask extends AbstractMonitorTask {

    @Autowired
    private MetaTableService  metaTableService;

    @Autowired
    private DataClientService dataClientService;

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
        List<MetaTableDTO> all = metaTableService.findAll();

        if (CollectionUtils.isEmpty(all))
            return;

        for (MetaTableDTO metaTableDTO : all) {
            List<Map<String, Object>> mapList = dataClientService.queryAll(metaTableDTO.getName());
            Debugger.print("TenantAppMetaSyncTask", "qurey meta list from table={} size={}",
                metaTableDTO.name, mapList.size());
            if (CollectionUtils.isEmpty(mapList)) {
                continue;
            }

            Set<String> appSets = new HashSet<>();

            Map<String, String> appTypes = new HashMap<>();
            for (Map<String, Object> map : mapList) {
                if (!map.containsKey("app")) {
                    continue;
                }

                String app = map.get("app").toString();
                appSets.add(app);

                if (map.containsKey("_type")) {
                    appTypes.put(app, map.get("_type").toString());
                }
            }

            Debugger.print("TenantAppMetaSyncTask", "qurey app list from table={} size={}",
                metaTableDTO.name, appSets.size());

            if (CollectionUtils.isEmpty(appSets)) {
                continue;
            }

            String tableName = metaTableDTO.getTenant() + "_app";

            Set<String> dbApps = getDbApps(tableName);

            List<Map<String, Object>> rows = new ArrayList<>();
            appSets.forEach(app -> {
                if (StringUtil.isBlank(app) || "-".equalsIgnoreCase(app)) {
                    return;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("_modifier", "admin");
                map.put("_modified", System.currentTimeMillis());
                map.put("_type", "app");
                map.put("app", app);

                Map<String, Object> labelMap = new HashMap<>();
                labelMap.put("machineType", appTypes.get(app));
                map.put("_label", labelMap);

                rows.add(map);

                dbApps.remove(app);
            });

            dataClientService.insertOrUpdate(tableName, rows);

            if (!CollectionUtils.isEmpty(dbApps)) {
                dataClientService.delete(tableName, new ArrayList<>(dbApps));
            }

        }
    }

    private Set<String> getDbApps(String tableName) {
        List<Map<String, Object>> dbLists = dataClientService.queryAll(tableName);

        Set<String> appSets = new HashSet<>();
        if (CollectionUtils.isEmpty(dbLists))
            return appSets;

        dbLists.forEach(db -> {
            if (!db.containsKey("app"))
                return;
            appSets.add(db.get("app").toString());
        });

        return appSets;
    }
}