/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.common.GaeaSqlTaskUtil;
import io.holoinsight.server.home.biz.plugin.config.LogPluginConfig;
import io.holoinsight.server.home.dal.model.dto.CustomPluginPeriodType;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.ExtraConfig;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2022-11-02 11:19:00
 */
@Slf4j
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

  public ExtraConfig extraConfig;

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
    From from = GaeaSqlTaskUtil.buildFrom(logPaths, logParse, extraConfig, whiteFilters,
        blackFilters, splitCols);
    Where where = GaeaSqlTaskUtil.buildWhere(logParse, splitColMap, collectMetric);
    GroupBy groupBy =
        GaeaSqlTaskUtil.buildGroupBy(logParse, extraConfig, splitColMap, collectMetric);
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

    String json = integrationPluginDTO.json;

    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return logPlugins;

    List<LogPluginConfig> multiLogPluginConfigs =
        J.fromJson(J.toJson(map.get("confs")), new TypeToken<List<LogPluginConfig>>() {}.getType());

    for (LogPluginConfig config : multiLogPluginConfigs) {
      if (null == config.conf)
        continue;

      CustomPluginConf customPluginConf =
          J.fromJson(J.toJson(config.conf), new TypeToken<CustomPluginConf>() {}.getType());

      addSpmColInPluginConf(customPluginConf);

      if (CollectionUtils.isEmpty(customPluginConf.collectMetrics))
        continue;
      CustomPluginPeriodType periodType = config.periodType;
      Map<String, Object> confMap = J.toMap(J.toJson(config.conf));
      try {
        if (null != confMap && confMap.containsKey("period")) {
          periodType = CustomPluginPeriodType.valueOf((String) confMap.get("period"));
        }
      } catch (Exception e) {
        log.warn("parse period error");
      }

      for (CollectMetric collectMetric : customPluginConf.getCollectMetrics()) {
        if (Boolean.TRUE == customPluginConf.spm
            && collectMetric.tableName.contains("successPercent")) {
          continue;
        }
        LogPlugin logPlugin = new LogPlugin();

        logPlugin.tenant = integrationPluginDTO.tenant;
        logPlugin.logPaths = customPluginConf.logPaths;
        logPlugin.blackFilters = customPluginConf.blackFilters;
        logPlugin.whiteFilters = customPluginConf.whiteFilters;
        logPlugin.extraConfig = customPluginConf.extraConfig;
        logPlugin.logParse = customPluginConf.logParse;
        logPlugin.splitCols = customPluginConf.splitCols;
        logPlugin.collectMetric = collectMetric;
        logPlugin.periodType = periodType;
        logPlugin.name = config.name;
        logPlugin.metricName = getMetricName(integrationPluginDTO.product.toLowerCase(),
            config.getName(), collectMetric.tableName);

        logPlugin.gaeaTableName =
            integrationPluginDTO.name + "_" + config.name + "_" + collectMetric.tableName;

        logPlugin.collectRange = customPluginConf.collectRanges;
        logPlugin.collectPlugin = SqlTask.class.getName();

        logPlugins.add(logPlugin);
      }

    }

    return logPlugins;
  }

  public String getMetricName(String product, String configName, String tableName) {
    if (StringUtils.isNotBlank(getPrefix())) {
      return String.join("_", getPrefix(), product, configName, tableName);
    }
    return String.join("_", product, configName, tableName);
  }

  public String getPrefix() {
    return null;
  }

  public void addSpmColInPluginConf(CustomPluginConf conf) {

  }

  @Autowired
  private MetricInfoService metricInfoService;

  private void saveMetricInfo(CustomPluginConf conf, CustomPluginPeriodType periodType,
      String tenant, String workspace, String product, String item, Boolean deleted) {
    List<CollectMetric> collectMetrics = conf.getCollectMetrics();

    for (CollectMetric collectMetric : collectMetrics) {
      try {
        MetricInfoDTO metricInfoDTO = new MetricInfoDTO();
        metricInfoDTO.setTenant("-");
        metricInfoDTO.setWorkspace("-");
        metricInfoDTO.setOrganization("-");
        metricInfoDTO.setProduct(product);

        metricInfoDTO.setMetricType("logdefault");
        if (Boolean.TRUE == conf.spm && collectMetric.tableName.contains("successPercent")) {
          metricInfoDTO.setMetricType("logspm");
        } else if (collectMetric.checkLogPattern()) {
          metricInfoDTO.setMetricType("logpattern");
        } else if (collectMetric.checkLogSample()) {
          metricInfoDTO.setMetricType("logsample");
        }
        metricInfoDTO.setMetric(collectMetric.getTableName());
        metricInfoDTO.setMetricTable(
            getMetricName(product.toLowerCase(), item, collectMetric.getTableName()));
        metricInfoDTO.setDeleted(deleted);
        metricInfoDTO.setDescription(collectMetric.tableName);
        metricInfoDTO.setUnit("number");
        metricInfoDTO.setPeriod(periodType.dataUnitMs / 1000);

        List<String> tags = new ArrayList<>(Arrays.asList("ip", "hostname", "namespace"));
        if (!CollectionUtils.isEmpty(collectMetric.tags)) {
          tags.addAll(collectMetric.tags);
        }
        metricInfoDTO.setTags(tags);

        MetricInfoDTO db = metricInfoService.queryByMetric(metricInfoDTO.getTenant(),
            metricInfoDTO.getWorkspace(), collectMetric.getTargetTable());
        if (null == db) {
          metricInfoService.create(metricInfoDTO);
        } else {
          metricInfoDTO.setId(db.id);
          metricInfoService.update(metricInfoDTO);
        }
      } catch (Exception e) {
        log.error("saveLogPluginMetricInfo error, {}, {}", collectMetric.getTargetTable(),
            e.getMessage(), e);
      }
    }
  }

  public void afterAction(IntegrationPluginDTO integrationPluginDTO) {

    String json = integrationPluginDTO.json;
    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return;

    List<LogPluginConfig> multiLogPluginConfigs =
        J.fromJson(J.toJson(map.get("confs")), new TypeToken<List<LogPluginConfig>>() {}.getType());

    for (LogPluginConfig config : multiLogPluginConfigs) {
      CustomPluginConf customPluginConf =
          J.fromJson(J.toJson(config.conf), new TypeToken<CustomPluginConf>() {}.getType());

      if (CollectionUtils.isEmpty(customPluginConf.collectMetrics))
        continue;

      saveMetricInfo(customPluginConf, config.getPeriodType(), integrationPluginDTO.tenant,
          integrationPluginDTO.workspace, integrationPluginDTO.product, config.name,
          integrationPluginDTO.status);
    }
  }

  @Override
  public Map<String, Object> getExecutorSelector() {
    if (this instanceof SlsLogPlugin) {
      return GaeaConvertUtil.getCenterExecutorSelector();
    }
    return GaeaConvertUtil.getLocalExecutorSelector();
  }
}
