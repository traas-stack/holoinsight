/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.alicloud.AliCloudTask;
import io.holoinsight.server.registry.model.integration.alicloud.AlicloudConf;
import io.holoinsight.server.registry.model.integration.alicloud.NameMetrics;
import io.holoinsight.server.registry.model.integration.alicloud.Range;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jsy1001de
 * @version 1.0: AlibabaCloudPlugin.java, v 0.1 2022年11月22日 上午11:14 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.AlibabaCloudPlugin", version = "1")
public class AlibabaCloudPlugin extends AbstractCentralIntegrationPlugin<AlibabaCloudPlugin> {

  public AliCloudTask aliCloudTask;

  @Override
  public AliCloudTask buildTask() {
    return aliCloudTask;
  }

  @Override
  public List<AlibabaCloudPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {

    List<AlibabaCloudPlugin> alibabaCloudPlugins = new ArrayList<>();
    AlibabaCloudPlugin alibabaCloudPlugin = new AlibabaCloudPlugin();
    {
      AliCloudTask aliCloudTask =
          J.fromJson(integrationPluginDTO.json, new TypeToken<AliCloudTask>() {}.getType());

      aliCloudTask.setExecuteRule(getExecuteRule());
      fillAlicloudRange(aliCloudTask);

      alibabaCloudPlugin.aliCloudTask = aliCloudTask;
      alibabaCloudPlugin.gaeaTableName = integrationPluginDTO.name;
      GaeaCollectRange gaeaCollectRange =
          J.fromJson(J.toJson(integrationPluginDTO.collectRange), GaeaCollectRange.class);
      alibabaCloudPlugin.collectRange =
          CollectionUtils.isEmpty(integrationPluginDTO.collectRange) ? new CloudMonitorRange()
              : gaeaCollectRange.cloudmonitor;
      alibabaCloudPlugin.collectPlugin = AliCloudTask.class.getName();
    }

    alibabaCloudPlugins.add(alibabaCloudPlugin);
    return alibabaCloudPlugins;
  }

  private void fillAlicloudRange(AliCloudTask aliCloudTask) {
    String fullRangeJson = MetaDictUtil.getStringValue("global_config", "alicloud_metrics");
    List<NameMetrics> fullRange =
        J.get().fromJson(fullRangeJson, (new TypeToken<List<NameMetrics>>() {}).getType());
    if (fullRange != null) {
      Map<String, NameMetrics> fullRangeMap =
          fullRange.stream().collect(Collectors.toMap(NameMetrics::getName, Function.identity()));
      List<AlicloudConf> confs = aliCloudTask.getConfs();
      if (confs != null) {
        confs.forEach(conf -> {
          Range range = conf.getRange() != null ? conf.getRange() : new Range();
          conf.setRange(range);
          if (CollectionUtils.isEmpty(conf.getRangeNames())) {
            List<String> names =
                Arrays.asList("elastic-compute-service", "redis-standard-1", "redis-standard-2",
                    "object-storage-service-1", "log-service", "application-load-balancer",
                    "server-load-balancer-1", "elasticsearch", "apsaradb-for-redis-3",
                    "object-storage-service-1", "rocketmq", "waf-1", "polardb-for-mysql");
            range.setNames(names);
          } else {
            range.setNames(conf.getRangeNames());
          }
          List<NameMetrics> nameMetrics = new ArrayList<>();
          range.getNames().forEach(name -> {
            NameMetrics metrics = fullRangeMap.get(name);
            if (metrics != null) {
              nameMetrics.add(metrics);
            }
          });
          range.setNameMetrics(nameMetrics);
        });
      }
    }
  }

}
