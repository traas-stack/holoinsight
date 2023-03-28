/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.listener;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.common.GaeaSqlTaskUtil;
import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知registry
 * 
 * @author jsy1001de
 * @version 1.0: CustomPluginUpdateListener.java, v 0.1 2022年03月15日 8:40 下午 jinsong.yjs Exp $
 */
@Component
@Slf4j
public class CustomPluginUpdateListener {

  @Autowired
  private GaeaCollectConfigService gaeaCollectConfigService;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(CustomPluginDTO customPluginDTO) {
    // 1. 转换模型
    // 2. 落库
    // 3. 通知registry
    try {
      // 转成采集模型
      CustomPluginConf conf = customPluginDTO.getConf();

      List<LogPath> logPaths = conf.logPaths;

      List<CollectMetric> collectMetrics = conf.getCollectMetrics();

      Map<String, SqlTask> sqlTaskMaps = new HashMap<>();
      for (CollectMetric collectMetric : collectMetrics) {
        SqlTask sqlTask = buildSqlTask(logPaths, collectMetric, customPluginDTO);

        String name = collectMetric.name;
        if (StringUtil.isNotBlank(collectMetric.tableName)) {
          name = collectMetric.tableName;
        }
        String tableName = String.format("%s_%s", name, customPluginDTO.id);
        sqlTaskMaps.put(tableName, sqlTask);
      }

      if (CollectionUtils.isEmpty(sqlTaskMaps))
        return;

      // 更新
      List<Long> upsert = upsert(sqlTaskMaps, customPluginDTO);

      // 通知registry
      notify(upsert);
    } catch (Exception e) {
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
      gaeaCollectConfigDTO.tenant = customPluginDTO.tenant;
      gaeaCollectConfigDTO.workspace = customPluginDTO.workspace;
      gaeaCollectConfigDTO.deleted = false;
      gaeaCollectConfigDTO.json = entry.getValue();
      gaeaCollectConfigDTO.tableName = entry.getKey();
      gaeaCollectConfigDTO.collectRange = GaeaConvertUtil.convertCollectRange(conf.collectRanges);
      gaeaCollectConfigDTO.executorSelector = GaeaSqlTaskUtil.convertExecutorSelector();
      gaeaCollectConfigDTO.version = 1L;
      gaeaCollectConfigDTO.refId = "custom_" + customPluginDTO.getId();

      byMap.remove(entry.getKey());
      // 下线配置直接置为 deleted=1
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

  private void notify(List<Long> upsertList) {

    // grpc 通知id更新
  }

  private SqlTask buildSqlTask(List<LogPath> logPaths, CollectMetric collectMetric,
      CustomPluginDTO customPluginDTO) {

    CustomPluginConf conf = customPluginDTO.getConf();

    Map<String, Map<String, SplitCol>> splitColMap =
        GaeaSqlTaskUtil.convertSplitColMap(conf.splitCols);
    Select select = GaeaSqlTaskUtil.buildSelect(conf.logParse, splitColMap, collectMetric);
    From from = GaeaSqlTaskUtil.buildFrom(logPaths, conf.logParse, conf.whiteFilters,
        conf.blackFilters, conf.splitCols);
    Where where = GaeaSqlTaskUtil.buildWhere(conf.logParse, splitColMap, collectMetric);
    GroupBy groupBy = GaeaSqlTaskUtil.buildGroupBy(conf.logParse, splitColMap, collectMetric);
    Window window = GaeaSqlTaskUtil.buildWindow(customPluginDTO.periodType.getDataUnitMs());
    Output output =
        GaeaSqlTaskUtil.buildOutput(getTargetTableName(customPluginDTO.id, collectMetric));
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

  private String getTargetTableName(Long id, CollectMetric collectMetric) {
    String targetTableName = String.format("%s_%s", collectMetric.tableName, id);
    if (!StringUtils.isEmpty(collectMetric.name)) {
      targetTableName = collectMetric.name;
    }
    return targetTableName;
  }
}
