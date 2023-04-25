/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.PortCheckPluginConfig;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.portcheck.PortCheckTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.registry.model.ExecuteRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: PortCheckPlugin.java, v 0.1 2022年11月18日 下午5:56 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.PortCheckPlugin", version = "1")
public class PortCheckPlugin extends AbstractLocalIntegrationPlugin<PortCheckPlugin> {

  public PortCheckTask portCheckTask;

  @Override
  public PortCheckTask buildTask() {
    return portCheckTask;
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.datasource;
  }


  @Override
  public List<PortCheckPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {
    List<PortCheckPlugin> portCheckPlugins = new ArrayList<>();

    String json = integrationPluginDTO.json;

    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return portCheckPlugins;

    List<PortCheckPluginConfig> portCheckPluginConfigs = J.fromJson(J.toJson(map.get("confs")),
        new TypeToken<List<PortCheckPluginConfig>>() {}.getType());

    for (PortCheckPluginConfig config : portCheckPluginConfigs) {
      PortCheckPlugin portCheckPlugin = new PortCheckPlugin();

      PortCheckTask portCheckTask = new PortCheckTask();
      {
        portCheckTask.port = config.port;
        portCheckTask.timeout = 3000L;
        portCheckTask.times = 1;
        portCheckTask.network = "tcp";
        portCheckTask.networkMode = "AGENT";

        ExecuteRule executeRule = new ExecuteRule();
        executeRule.setType("fixedRate");
        executeRule.setFixedRate(60000);
        portCheckTask.setExecuteRule(executeRule);
      }

      {
        portCheckPlugin.tenant = integrationPluginDTO.tenant;
        portCheckPlugin.name = integrationPluginDTO.product.toLowerCase();
        portCheckPlugin.metricName =
            String.join("_", integrationPluginDTO.product.toLowerCase(), "tcp_ping");
        portCheckPlugin.gaeaTableName = integrationPluginDTO.name + "_" + config.port;

        portCheckPlugin.collectRange = GaeaConvertUtil.convertCloudMonitorRange(
            integrationPluginDTO.getTenant() + "_server", config.getMetaLabel(), config.range);

        portCheckPlugin.portCheckTask = portCheckTask;
        portCheckPlugin.collectPlugin = "dialcheck";

      }


      portCheckPlugins.add(portCheckPlugin);
    }

    return portCheckPlugins;
  }
}
