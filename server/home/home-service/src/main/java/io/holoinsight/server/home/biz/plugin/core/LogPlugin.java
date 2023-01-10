/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.common.GaeaSqlTaskUtil;
import io.holoinsight.server.home.biz.plugin.config.LogPluginConfig;
import io.holoinsight.server.home.dal.model.dto.CustomPluginPeriodType;
import io.holoinsight.server.home.dal.model.dto.IntegrationConfig;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.Filter;
import io.holoinsight.server.home.dal.model.dto.conf.LogParse;
import io.holoinsight.server.home.dal.model.dto.conf.LogPath;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.registry.model.From;
import io.holoinsight.server.registry.model.GroupBy;
import io.holoinsight.server.registry.model.Output;
import io.holoinsight.server.registry.model.Select;
import io.holoinsight.server.registry.model.SqlTask;
import io.holoinsight.server.registry.model.Where;
import io.holoinsight.server.registry.model.Window;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2022-11-02 11:19:00
 */
public class LogPlugin extends AbstractLocalIntegrationPlugin<LogPlugin> {

  public LogPlugin() {}

  /**
   * 日志路径
   */
  public List<LogPath> logPaths;

  /**
   * 前置过滤黑名单
   */
  public List<Filter> blackFilters;

  /**
   * 前置过滤白名单
   */
  public List<Filter> whiteFilters;

  /**
   * 日志切分规则 分隔符切分/左起右至/正则表达式
   */
  public LogParse logParse = new LogParse();

  /**
   * 全局维度定义
   */
  public List<CustomPluginConf.SplitCol> splitCols;

  /**
   * 监控指标定义
   */
  public CollectMetric collectMetric;

  /**
   * 时间粒度
   */
  public CustomPluginPeriodType periodType;

  @Override
  public SqlTask buildTask() {
    Map<String, Map<String, CustomPluginConf.SplitCol>> splitColMap =
        GaeaSqlTaskUtil.convertSplitColMap(splitCols);
    Select select = GaeaSqlTaskUtil.buildSelect(logParse, splitColMap, collectMetric);
    From from = GaeaSqlTaskUtil.buildFrom(logPaths, logParse, whiteFilters, blackFilters);
    Where where = GaeaSqlTaskUtil.buildWhere(logParse, splitColMap, collectMetric);
    GroupBy groupBy = GaeaSqlTaskUtil.buildGroupBy(logParse, splitColMap, collectMetric);
    Window window = GaeaSqlTaskUtil.buildWindow(periodType.getDataUnitMs());
    Output output = GaeaSqlTaskUtil.buildOutput(metricName);

    SqlTask sqlTask = new SqlTask();
    {
      sqlTask.setSelect(select);
      sqlTask.setFrom(from);
      sqlTask.setWhere(where);
      sqlTask.setGroupBy(groupBy);
      sqlTask.setWindow(window);
      sqlTask.setOutput(output);
      sqlTask.setExecuteRule(GaeaSqlTaskUtil.buildExecuteRule());
    }

    return sqlTask;
  }

  public List<LogPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {
    List<LogPlugin> logPlugins = new ArrayList<>();

    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("version", integrationPluginDTO.version);
    columnMap.put("name", integrationPluginDTO.product);
    List<IntegrationProductDTO> byMap = integrationProductService.findByMap(columnMap);
    if (CollectionUtils.isEmpty(byMap))
      return logPlugins;

    String json = integrationPluginDTO.json;

    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return logPlugins;

    List<LogPluginConfig> logPluginConfigs =
        J.fromJson(J.toJson(map.get("confs")), new TypeToken<List<LogPluginConfig>>() {}.getType());

    Map<String, LogPluginConfig> logPluginConfigMap = new HashMap<>();
    for (LogPluginConfig config : logPluginConfigs) {
      logPluginConfigMap.put(config.getName(), config);
    }

    IntegrationProductDTO productDTO = byMap.get(0);

    List<IntegrationConfig> configList = productDTO.getForm().getConfigList();

    for (IntegrationConfig config : configList) {
      CustomPluginConf customPluginConf =
          J.fromJson(J.toJson(config.conf), new TypeToken<CustomPluginConf>() {}.getType());

      if (CollectionUtils.isEmpty(customPluginConf.collectMetrics))
        continue;

      for (CollectMetric collectMetric : customPluginConf.getCollectMetrics()) {
        LogPluginConfig logPluginConfig = logPluginConfigMap.get(config.getName());

        if (null == logPluginConfig)
          continue;

        LogPlugin logPlugin = new LogPlugin();

        logPlugin.tenant = integrationPluginDTO.tenant;
        logPlugin.logPaths = customPluginConf.logPaths;
        logPlugin.blackFilters = customPluginConf.blackFilters;
        logPlugin.whiteFilters = customPluginConf.whiteFilters;
        logPlugin.logParse = customPluginConf.logParse;
        logPlugin.splitCols = customPluginConf.splitCols;
        logPlugin.collectMetric = collectMetric;
        logPlugin.periodType = config.periodType;
        logPlugin.metricName =
            String.join("_", ANTGROUP_METRIC_PREFIX, integrationPluginDTO.product.toLowerCase(),
                config.getName(), collectMetric.targetTable);

        logPlugin.gaeaTableName =
            integrationPluginDTO.name + "_" + config.name + "_" + collectMetric.targetTable;

        logPlugin.collectRange =
            GaeaConvertUtil.convertCloudMonitorRange(integrationPluginDTO.getTenant() + "_server",
                logPluginConfig.getMetaLabel(), logPluginConfig.range);
        logPlugin.collectPlugin = SqlTask.class.getName();

        logPlugins.add(logPlugin);
      }

    }

    return logPlugins;
  }
}
