/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.home.biz.plugin.PluginRepository;
import io.holoinsight.server.home.biz.plugin.core.AbstractIntegrationPlugin;
import io.holoinsight.server.home.biz.service.IntegrationGeneratedService;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.biz.service.TenantService;
import io.holoinsight.server.home.common.util.cache.local.CommonLocalCache;
import io.holoinsight.server.home.common.util.scope.MonitorEnv;
import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.TenantDTO;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.holoinsight.server.home.common.util.cache.local.CacheConst.APP_META_KEY;
import static io.holoinsight.server.home.common.util.cache.local.CacheConst.INTEGRATION_GENERATED_CACHE_KEY;


/**
 *
 * @author jsy1001de
 * @version 1.0: TenantIntegrationGeneratedTask.java, v 0.1 2022年06月30日 4:51 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
@TaskHandler(TaskEnum.TENANT_INTEGRATION_GENERATED)
public class TenantIntegrationGeneratedTask extends AbstractMonitorTask {

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private IntegrationPluginService integrationPluginService;

  @Autowired
  private IntegrationGeneratedService integrationGeneratedService;

  @Autowired
  private PluginRepository pluginRepository;

  @Autowired
  private TenantService tenantService;

  public TenantIntegrationGeneratedTask() {
    super(1, 2, TaskEnum.TENANT_INTEGRATION_GENERATED);
  }

  @Override
  public boolean needRun() {
    return MonitorEnv.isSaasFactoryEnv();
  }

  public long getTaskPeriod() {
    return TEN_MINUTE;
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
        return "TenantIntegrationGeneratedTask";
      }
    });
    return jobs;
  }

  private void syncAoAction() {

    Map<String, Long> fromDb = getFromDb();
    Set<String> fromDbKeys = fromDb.keySet();
    log.info("fromDb: " + fromDbKeys.size());
    Map<String, IntegrationGeneratedDTO> outMaps = getIntegrationGeneratedList();
    log.info("outMaps: " + outMaps.size());
    List<IntegrationGeneratedDTO> newList = new ArrayList<>();
    for (String outKeys : outMaps.keySet()) {
      if (fromDbKeys.contains(outKeys)) {
        fromDbKeys.remove(outKeys);
      } else {
        newList.add(outMaps.get(outKeys));
      }
    }

    // create
    if (!CollectionUtils.isEmpty(newList)) {
      log.info("newList: " + newList.size());
      for (IntegrationGeneratedDTO integrationGeneratedDTO : newList) {
        integrationGeneratedService.insert(integrationGeneratedDTO);
      }
    }

    // delete
    if (!CollectionUtils.isEmpty(fromDbKeys)) {
      log.info("deleteList: " + fromDbKeys.size());
      for (String dbkey : fromDbKeys) {
        IntegrationGenerated integrationGenerated = new IntegrationGenerated();
        integrationGenerated.setDeleted(true);
        integrationGenerated.setId(fromDb.get(dbkey));
        integrationGeneratedService.updateById(integrationGenerated);
      }
    }
  }

  private Map<String, Long> getFromDb() {
    List<TenantDTO> tenantDTOS = tenantService.queryAll();
    Map<String, Long> dbUks = new HashMap<>();

    for (TenantDTO tenantDTO : tenantDTOS) {
      List<IntegrationGenerated> dblist =
          integrationGeneratedService.queryByTenant(tenantDTO.getCode());
      if (CollectionUtils.isEmpty(dblist)) {
        continue;
      }

      for (IntegrationGenerated generated : dblist) {
        dbUks.put(String.format("%s_%s_%s_%s", generated.getTenant(), generated.getProduct(),
            generated.getItem(), generated.getName()), generated.getId());
      }
    }

    return dbUks;
  }

  private Map<String, IntegrationGeneratedDTO> getIntegrationGeneratedList() {

    Map<String, IntegrationGeneratedDTO> dtoMap = new HashMap<>();
    List<IntegrationGeneratedDTO> generateds = new ArrayList<>();
    List<TenantDTO> tenantDTOS = tenantService.queryAll();
    for (TenantDTO tenantDTO : tenantDTOS) {

      String tableName = tenantDTO.code + "_app";

      Map<String, String> dbAppMaps = getDbApps(tableName);
      if (CollectionUtils.isEmpty(dbAppMaps))
        continue;

      for (Map.Entry<String, String> entry : dbAppMaps.entrySet()) {

        if (entry.getValue().equalsIgnoreCase("VM")) {
          generateds.add(generated(tenantDTO.getCode(), entry.getKey(), "vmsystem", "System"));
        } else {
          generateds.add(generated(tenantDTO.getCode(), entry.getKey(), "podsystem", "System"));
        }

        generateds.add(generated(tenantDTO.getCode(), entry.getKey(), "logpattern", "LogPattern"));

        generateds.add(generated(tenantDTO.getCode(), entry.getKey(), "portcheck", "PortCheck"));
      }

      List<IntegrationPluginDTO> integrationPluginDTOS =
          integrationPluginService.queryByTenant(tenantDTO.getCode());

      if (CollectionUtils.isEmpty(integrationPluginDTOS))
        continue;

      for (IntegrationPluginDTO integrationPluginDTO : integrationPluginDTOS) {
        AbstractIntegrationPlugin plugin = (AbstractIntegrationPlugin) this.pluginRepository
            .getTemplate(integrationPluginDTO.type, integrationPluginDTO.version);

        if (null == plugin)
          continue;
        List<AbstractIntegrationPlugin> abstractIntegrationPlugins =
            plugin.genPluginList(integrationPluginDTO);
        if (CollectionUtils.isEmpty(abstractIntegrationPlugins))
          continue;

        Set<String> appSets = new HashSet<>();
        for (AbstractIntegrationPlugin integrationPlugin : abstractIntegrationPlugins) {
          GaeaCollectRange gaeaCollectRange = integrationPlugin.getGaeaCollectRange();

          if (gaeaCollectRange.getType().equalsIgnoreCase("central")
              || null == gaeaCollectRange.getCloudmonitor()
              || StringUtils.isBlank(gaeaCollectRange.getCloudmonitor().table))
            continue;

          Set<String> collectApps = getCollectApps(gaeaCollectRange.getCloudmonitor());

          if (CollectionUtils.isEmpty(collectApps))
            continue;

          appSets.addAll(collectApps);
        }

        for (String app : appSets) {
          generateds.add(generated(tenantDTO.getCode(), app,
              integrationPluginDTO.getProduct().toLowerCase(), integrationPluginDTO.getProduct()));
        }

      }
    }

    for (IntegrationGeneratedDTO generated : generateds) {
      dtoMap.put(String.format("%s_%s_%s_%s", generated.getTenant(), generated.getProduct(),
          generated.getItem(), generated.getName()), generated);
    }
    return dtoMap;
  }

  private IntegrationGeneratedDTO generated(String tenant, String name, String item,
      String product) {
    IntegrationGeneratedDTO integrationGenerated = new IntegrationGeneratedDTO();
    {
      integrationGenerated.setTenant(tenant);
      integrationGenerated.setName(name);
      integrationGenerated.setProduct(product);
      integrationGenerated.setItem(item);
      integrationGenerated.setConfig(new HashMap<>());

      integrationGenerated.setDeleted(false);
      integrationGenerated.setCustom(false);
      integrationGenerated.setGmtCreate(new Date());
      integrationGenerated.setGmtModified(new Date());
      integrationGenerated.setCreator("system");
      integrationGenerated.setModifier("system");

    }

    return integrationGenerated;
  }

  private Set<String> getCollectApps(CloudMonitorRange cloudMonitorRange) {

    Set<String> o = CommonLocalCache
        .get(INTEGRATION_GENERATED_CACHE_KEY + MD5Hash.getMD5(J.toJson(cloudMonitorRange)));

    if (!CollectionUtils.isEmpty(o)) {
      return o;
    }

    List<Map<String, Object>> mapList = new ArrayList<>();
    if (CollectionUtils.isEmpty(cloudMonitorRange.condition)) {
      List<Map<String, Object>> metaList = dataClientService.queryAll(cloudMonitorRange.getTable());
      if (CollectionUtils.isEmpty(metaList))
        return new HashSet<>();

      mapList.addAll(metaList);

    } else {
      QueryExample queryExample = new QueryExample();
      queryExample.getParams().putAll(cloudMonitorRange.getCondition().get(0));
      List<Map<String, Object>> metaList =
          dataClientService.queryByExample(cloudMonitorRange.getTable(), queryExample);
      if (CollectionUtils.isEmpty(metaList))
        return new HashSet<>();
      mapList.addAll(metaList);
    }
    Set<String> appSets = new HashSet<>();

    for (Map<String, Object> map : mapList) {
      if (!map.containsKey("app")) {
        continue;
      }

      String app = map.get("app").toString();
      appSets.add(app);
    }

    CommonLocalCache.put(
        MD5Hash
            .getMD5(INTEGRATION_GENERATED_CACHE_KEY + MD5Hash.getMD5(J.toJson(cloudMonitorRange))),
        appSets, 10, TimeUnit.MINUTES);

    return appSets;
  }

  private Map<String, String> getDbApps(String tableName) {

    Map<String, String> o = CommonLocalCache.get(APP_META_KEY + tableName);
    if (!CollectionUtils.isEmpty(o)) {
      return o;
    }

    List<Map<String, Object>> dbLists = dataClientService.queryAll(tableName);

    if (CollectionUtils.isEmpty(dbLists))
      return new HashMap<>();

    Map<String, String> appMaps = new HashMap<>();

    dbLists.forEach(db -> {
      if (!db.containsKey("app"))
        return;

      if (!db.containsKey("_label"))
        return;

      Map<String, Object> map = J.toMap(J.toJson(db.get("_label")));

      if (!map.containsKey("machineType"))
        return;

      appMaps.put(db.get("app").toString(), map.getOrDefault("machineType", "-").toString());
    });

    CommonLocalCache.put(MD5Hash.getMD5(INTEGRATION_GENERATED_CACHE_KEY + tableName), appMaps, 10,
        TimeUnit.MINUTES);

    return appMaps;
  }
}
