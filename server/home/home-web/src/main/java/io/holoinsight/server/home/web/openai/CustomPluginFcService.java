/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.openai.OpenAiService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.CustomPluginMapper;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.CustomPluginPeriodType;
import io.holoinsight.server.home.dal.model.dto.CustomPluginStatus;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.Filter;
import io.holoinsight.server.home.dal.model.dto.conf.LogPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.web.controller.UserinfoVerificationFacadeImpl.generateVerificationCode;

/**
 * @author masaimu
 * @version 2023-06-20 13:55:00
 */
@Service
@Slf4j
public class CustomPluginFcService {

  @Resource
  private CustomPluginMapper customPluginMapper;

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private OpenAiService openAiService;

  public String queryCustomPlugin(Map<String, Object> paramMap) {
    String requestId = (String) paramMap.get("requestId");
    log.info("{} paramMap: {}", requestId, J.toJson(paramMap));
    String name = (String) paramMap.get("name");

    QueryWrapper<CustomPlugin> queryWrapper = new QueryWrapper<>();
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms != null) {
      queryWrapper.eq("tenant", ms.getTenant());
    }

    List<CustomPlugin> customPlugins = this.customPluginMapper.selectList(queryWrapper);

    if (StringUtils.isBlank(name)) {
      return "The name of log monitoring is empty";
    }
    String original = "[" + name + "]";

    List<String> combinations = new ArrayList<>();
    Map<String, CustomPlugin> map = new HashMap<>();
    for (CustomPlugin item : customPlugins) {
      String descKey = "[" + item.name + "]";
      combinations.add(descKey + "\n");
      map.put(descKey, item);
    }

    String key = this.openAiService.getSimilarKey(original, combinations, requestId);
    CustomPlugin customPlugin = map.get(key);
    if (customPlugin == null) {
      return null;
    }
    return J.toJson(customPlugin);
  }

  public String createCustomPlugin(Map<String, Object> paramMap) {
    String requestId = (String) paramMap.get("requestId");
    log.info("{} paramMap: {}", requestId, J.toJson(paramMap));
    String name = (String) paramMap.get("name");
    String logPath = (String) paramMap.get("logPath");
    List<String> keywords = (List<String>) paramMap.get("keywords");
    String appName = (String) paramMap.get("appName");
    String metric = (String) paramMap.getOrDefault("metric", getRandomMetric());
    String periodType = (String) paramMap.getOrDefault("periodType", "MINUTE");

    if (StringUtils.isBlank(logPath)) {
      return "Fail to create log monitoring configuration caused by log path is empty.";
    } else if (StringUtils.isBlank(appName)) {
      return "Fail to create log monitoring configuration caused by app name is empty.";
    }

    String tenant = RequestContext.getContext().ms.getTenant();
    String workspace = RequestContext.getContext().ms.getWorkspace();

    CustomPluginDTO insertItem = new CustomPluginDTO();
    insertItem.tenant = tenant;
    insertItem.workspace = workspace;
    insertItem.name = name;
    insertItem.pluginType = "custom";
    insertItem.status = CustomPluginStatus.ONLINE;
    insertItem.periodType = CustomPluginPeriodType.valueOf(periodType);

    insertItem.conf = buildCustomPluginConf(logPath, keywords, appName, metric, tenant);

    insertItem.creator = RequestContext.getContext().mu.getLoginName();
    insertItem.modifier = RequestContext.getContext().mu.getLoginName();
    insertItem.gmtCreate = new Date();
    insertItem.gmtModified = new Date();

    CustomPluginDTO result = this.customPluginService.create(insertItem);
    return J.toJson(result);
  }

  private String getRandomMetric() {
    return "metric_" + generateVerificationCode();
  }

  private CustomPluginConf buildCustomPluginConf(String logPath, List<String> keywords,
      String appName, String metric, String tenant) {
    CustomPluginConf conf = new CustomPluginConf();
    LogPath path = new LogPath();
    path.type = "path";
    path.path = logPath;
    conf.logPaths = Collections.singletonList(path);
    conf.blackFilters = new ArrayList<>();
    if (!CollectionUtils.isEmpty(keywords)) {
      Filter white = new Filter();
      white.type = "CONTAINS";
      white.values = new ArrayList<>(keywords);
      conf.whiteFilters = Collections.singletonList(white);
    }
    CloudMonitorRange range = new CloudMonitorRange();
    range.table = tenant + "_server";
    range.condition = Collections
        .singletonList(Collections.singletonMap("app", Collections.singletonList(appName)));
    conf.collectRanges = range;

    CollectMetric collectMetric = new CollectMetric();
    collectMetric.tableName = metric;
    collectMetric.metricType = "count";
    CollectMetric.Metric metric1 = new CollectMetric.Metric();
    metric1.name = "value";
    metric1.func = "count";
    collectMetric.metrics = Collections.singletonList(metric1);
    conf.collectMetrics = Collections.singletonList(collectMetric);

    return conf;
  }
}
