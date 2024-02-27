/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.listener;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.common.AggTaskUtil;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.common.GaeaSqlTaskUtil;
import io.holoinsight.server.home.biz.service.AggTaskV1Service;
import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.model.dto.AggTaskV1DTO;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.CustomPluginStatus;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
import io.holoinsight.server.home.dal.model.dto.conf.LogPath;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.holoinsight.server.registry.model.ExecuteRule;
import io.holoinsight.server.registry.model.From;
import io.holoinsight.server.registry.model.GroupBy;
import io.holoinsight.server.registry.model.Output;
import io.holoinsight.server.registry.model.Select;
import io.holoinsight.server.registry.model.SqlTask;
import io.holoinsight.server.registry.model.Where;
import io.holoinsight.server.registry.model.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notify registry
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginUpdateListener.java, v 0.1 2022-03-15 8:40 pm jinsong.yjs Exp $
 */
@Component
@Slf4j
public class CustomPluginUpdateListener {

  @Autowired
  private GaeaCollectConfigService gaeaCollectConfigService;

  @Autowired
  private AggTaskV1Service aggTaskV1Service;

  @Autowired
  private MetricInfoService metricInfoService;

  @Autowired
  private TenantInitService tenantInitService;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(CustomPluginDTO customPluginDTO) {

    // 0. save metricInfo
    saveMetricInfo(customPluginDTO);

    // 1. conversion model
    // 2. update database
    // 3. notify registry
    try {
      // convert to Acquisition Model
      CustomPluginConf conf = customPluginDTO.getConf();

      List<LogPath> logPaths = conf.logPaths;

      List<CollectMetric> collectMetrics = conf.getCollectMetrics();

      Map<String, SqlTask> sqlTaskMaps = new HashMap<>();
      Map<String, AggTask> aggTaskMaps = new HashMap<>();
      for (CollectMetric collectMetric : collectMetrics) {
        if (Boolean.TRUE == conf.spm && collectMetric.targetTable.contains("successPercent")) {
          continue;
        }
        SqlTask sqlTask = buildSqlTask(logPaths, collectMetric, customPluginDTO);

        String name = collectMetric.name;
        if (StringUtil.isNotBlank(collectMetric.tableName)) {
          name = collectMetric.tableName;
        }
        String tableName = String.format("%s_%s", name, customPluginDTO.id);
        sqlTaskMaps.put(tableName, sqlTask);

        if (null != collectMetric.getCalculate() && Boolean.TRUE == collectMetric.getCalculate()) {
          AggTask aggTask = buildAggTask(collectMetric, customPluginDTO);
          aggTaskMaps.put(collectMetric.logCalculate.aggTableName, aggTask);
        }
      }

      if (CollectionUtils.isEmpty(sqlTaskMaps))
        return;

      // update
      List<Long> gaeaTask = upsert(sqlTaskMaps, customPluginDTO);
      List<Long> aggTask = upsertAgg(aggTaskMaps, customPluginDTO);

      log.info("gaeaTask,upsert, {}", gaeaTask);
      log.info("aggTask,upsert, {}", aggTask);
      // notify registry
      notify(gaeaTask);
    } catch (Throwable e) {
      log.error("fail to convert customPlugin to gaeaCollectConfig id {} for {}",
          customPluginDTO.id, e.getMessage(), e);
    }
  }

  private List<Long> upsert(Map<String, SqlTask> sqlTasks, CustomPluginDTO customPluginDTO) {

    CustomPluginConf conf = customPluginDTO.conf;

    List<GaeaCollectConfigDTO> byRefId =
        gaeaCollectConfigService.findByRefId("custom_" + customPluginDTO.getId());

    Map<String, GaeaCollectConfigDTO> byMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(byRefId)) {
      byRefId.forEach(by -> {
        byMap.put(by.getTableName(), by);
      });
    }

    List<Long> upsertList = new ArrayList<>();
    for (Map.Entry<String, SqlTask> entry : sqlTasks.entrySet()) {
      GaeaCollectConfigDTO gaeaCollectConfigDTO = new GaeaCollectConfigDTO();
      gaeaCollectConfigDTO.tenant = tenantInitService.getTsdbTenant(customPluginDTO.tenant);
      gaeaCollectConfigDTO.workspace = customPluginDTO.workspace;
      gaeaCollectConfigDTO.deleted = false;
      gaeaCollectConfigDTO.json = entry.getValue();
      gaeaCollectConfigDTO.tableName = entry.getKey();
      gaeaCollectConfigDTO.collectRange = GaeaConvertUtil.convertCollectRange(conf.collectRanges);
      gaeaCollectConfigDTO.executorSelector = GaeaSqlTaskUtil.convertExecutorSelector();
      gaeaCollectConfigDTO.version = 1L;
      gaeaCollectConfigDTO.refId = "custom_" + customPluginDTO.getId();

      byMap.remove(entry.getKey());
      // The offline configuration is directly set to deleted=1
      if (customPluginDTO.getStatus() == CustomPluginStatus.OFFLINE) {
        Long aLong = gaeaCollectConfigService.updateDeleted(gaeaCollectConfigDTO.tableName);
        if (null != aLong)
          upsertList.add(aLong);
        continue;
      }
      GaeaCollectConfigDTO upsert = gaeaCollectConfigService.upsert(gaeaCollectConfigDTO);
      if (null != upsert) {
        upsertList.add(upsert.id);
      }
    }

    if (!CollectionUtils.isEmpty(byMap)) {
      byMap.forEach((key, val) -> {
        gaeaCollectConfigService.updateDeleted(val.getId());
        upsertList.add(val.getId());
      });
    }

    return upsertList;
  }

  private SqlTask buildSqlTask(List<LogPath> logPaths, CollectMetric collectMetric,
      CustomPluginDTO customPluginDTO) {

    CustomPluginConf conf = customPluginDTO.getConf();

    Map<String, Map<String, SplitCol>> splitColMap =
        GaeaSqlTaskUtil.convertSplitColMap(conf.splitCols);
    Select select = GaeaSqlTaskUtil.buildSelect(conf.logParse, splitColMap, collectMetric);
    From from = GaeaSqlTaskUtil.buildFrom(logPaths, conf.logParse, conf.extraConfig,
        conf.whiteFilters, conf.blackFilters, conf.splitCols);
    Where where = GaeaSqlTaskUtil.buildWhere(conf.logParse, splitColMap, collectMetric);
    GroupBy groupBy = GaeaSqlTaskUtil.buildGroupBy(conf.logParse, conf.getExtraConfig(),
        splitColMap, collectMetric);
    Window window = GaeaSqlTaskUtil.buildWindow(customPluginDTO.periodType.getDataUnitMs());
    Output output = GaeaSqlTaskUtil.buildOutput(collectMetric.getTargetTable());
    ExecuteRule executeRule = GaeaSqlTaskUtil.buildExecuteRule();

    SqlTask sqlTask = new SqlTask();
    {
      sqlTask.setSelect(select);
      sqlTask.setFrom(from);
      sqlTask.setWhere(where);
      sqlTask.setGroupBy(groupBy);
      sqlTask.setWindow(window);
      sqlTask.setOutput(output);
      sqlTask.setExecuteRule(executeRule);
    }

    return sqlTask;
  }

  private AggTask buildAggTask(CollectMetric collectMetric, CustomPluginDTO customPluginDTO) {
    AggTask aggTask = new AggTask();
    String tableName = String.format("%s_%s", collectMetric.getTableName(), customPluginDTO.id);
    aggTask.setPartitionKeys(AggTaskUtil.buildPartition(collectMetric));
    aggTask.setSelect(AggTaskUtil.buildSelect(collectMetric));
    aggTask.setFrom(AggTaskUtil.buildFrom(tableName, collectMetric,
        tenantInitService.getAggCompletenessTags()));
    aggTask.setWhere(AggTaskUtil.buildWhere(collectMetric));
    aggTask.setGroupBy(
        AggTaskUtil.buildGroupBy(collectMetric, tenantInitService.getAggDefaultGroupByTags()));
    aggTask.setWindow(AggTaskUtil.buildWindow(customPluginDTO.getPeriodType().dataUnitMs));
    aggTask.setOutput(AggTaskUtil.buildOutput(collectMetric));
    aggTask.setFillZero(AggTaskUtil.buildFillZero(collectMetric));
    return aggTask;
  }

  private List<Long> upsertAgg(Map<String, AggTask> aggTasks, CustomPluginDTO customPluginDTO) {

    if (CollectionUtils.isEmpty(aggTasks))
      return new ArrayList<>();

    List<AggTaskV1DTO> byRefId = aggTaskV1Service.findByRefId("custom_" + customPluginDTO.getId());

    Map<String, AggTaskV1DTO> byMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(byRefId)) {
      byRefId.forEach(by -> {
        byMap.put(by.getAggId(), by);
      });
    }

    List<Long> upsertList = new ArrayList<>();
    for (Map.Entry<String, AggTask> entry : aggTasks.entrySet()) {
      AggTaskV1DTO aggTaskV1DTO = new AggTaskV1DTO();
      aggTaskV1DTO.setDeleted(false);
      aggTaskV1DTO.setJson(entry.getValue());
      aggTaskV1DTO.setAggId(entry.getKey());
      aggTaskV1DTO.setVersion(1L);
      aggTaskV1DTO.setRefId("custom_" + customPluginDTO.getId());

      byMap.remove(entry.getKey());
      // The offline configuration is directly set to deleted=1
      if (customPluginDTO.getStatus() == CustomPluginStatus.OFFLINE) {
        Long aLong = aggTaskV1Service.updateDeleted(entry.getKey());
        if (null != aLong)
          upsertList.add(aLong);
        continue;
      }
      AggTaskV1DTO upsert = aggTaskV1Service.upsert(aggTaskV1DTO);
      if (null != upsert) {
        upsertList.add(upsert.getId());
      }
    }

    if (!CollectionUtils.isEmpty(byMap)) {
      byMap.forEach((key, val) -> {
        aggTaskV1Service.updateDeleted(val.getId());
        upsertList.add(val.getId());
      });
    }

    return upsertList;
  }

  private void saveMetricInfo(CustomPluginDTO customPluginDTO) {
    CustomPluginConf conf = customPluginDTO.getConf();
    List<CollectMetric> collectMetrics = conf.getCollectMetrics();

    for (CollectMetric collectMetric : collectMetrics) {
      saveMetricByCollectMetric(customPluginDTO, collectMetric, conf.spm, false);
      if (null != collectMetric.getCalculate() && Boolean.TRUE == collectMetric.getCalculate()) {
        saveMetricByCollectMetric(customPluginDTO, collectMetric, conf.spm, true);
      }
    }
  }


  private void saveMetricByCollectMetric(CustomPluginDTO customPluginDTO,
      CollectMetric collectMetric, Boolean isSpm, Boolean isAgg) {
    String tableName = collectMetric.getTableName();
    String targetTable = collectMetric.getTargetTable();
    if (isAgg) {
      targetTable = collectMetric.logCalculate.getAggTableName();
    }

    try {
      MetricInfoDTO metricInfoDTO = new MetricInfoDTO();
      metricInfoDTO.setTenant(customPluginDTO.getTenant());
      metricInfoDTO.setWorkspace(
          null == customPluginDTO.getWorkspace() ? "-" : customPluginDTO.getWorkspace());
      metricInfoDTO.setOrganization("-");
      metricInfoDTO.setProduct("logmonitor");

      metricInfoDTO.setMetricType("logdefault");
      if (Boolean.TRUE == isSpm && collectMetric.targetTable.contains("successPercent")) {
        metricInfoDTO.setMetricType("logspm");
      } else if (collectMetric.checkLogPattern()) {
        metricInfoDTO.setMetricType("logpattern");
      } else if (collectMetric.checkLogSample()) {
        metricInfoDTO.setMetricType("logsample");
      }
      if (isAgg) {
        Map<String, Object> extInfo = new HashMap<>();
        extInfo.put("isAgg", true);
        metricInfoDTO.setExtInfo(extInfo);
      }

      metricInfoDTO.setMetric(tableName);
      metricInfoDTO.setMetricTable(targetTable);
      metricInfoDTO.setDeleted(customPluginDTO.status == CustomPluginStatus.OFFLINE);
      metricInfoDTO.setDescription(customPluginDTO.getName() + "_" + tableName);
      metricInfoDTO.setUnit("number");
      metricInfoDTO.setPeriod(customPluginDTO.getPeriodType().dataUnitMs / 1000);

      List<String> tags = new ArrayList<>(Arrays.asList("ip", "hostname", "namespace"));
      if (!isAgg && !CollectionUtils.isEmpty(collectMetric.tags)) {
        tags.addAll(collectMetric.tags);
      }
      if (!isAgg && !CollectionUtils.isEmpty(collectMetric.refTags)) {
        tags.addAll(collectMetric.refTags);
      }
      metricInfoDTO.setTags(tags);
      metricInfoDTO.setRef(String.valueOf(customPluginDTO.getId()));
      MetricInfoDTO db = metricInfoService.queryByMetric(metricInfoDTO.getTenant(),
          metricInfoDTO.getWorkspace(), targetTable);
      if (null == db) {
        metricInfoService.create(metricInfoDTO);
      } else {
        metricInfoDTO.setId(db.id);
        metricInfoService.update(metricInfoDTO);
      }
    } catch (Exception e) {
      log.error("saveLogMetricInfo error, {}, {}", targetTable, e.getMessage(), e);
    }
  }

  private void notify(List<Long> upsertList) {

    // grpc notification id update
  }
}
