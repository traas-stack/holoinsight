/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.MetaDictKey;
import io.holoinsight.server.home.biz.common.MetaDictType;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.plugin.config.PortCheckPluginConfig;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.IntegrationTransForm;
import io.holoinsight.server.registry.model.integration.portcheck.PortCheckTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.registry.model.ExecuteRule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public TenantInitService tenantInitService;

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

    int i = 0;
    for (PortCheckPluginConfig config : portCheckPluginConfigs) {
      PortCheckPlugin portCheckPlugin = new PortCheckPlugin();

      PortCheckTask portCheckTask = new PortCheckTask();
      {
        portCheckTask.port = config.port;
        portCheckTask.ports = config.ports;
        portCheckTask.timeout = 3000L;
        portCheckTask.times = 1;
        portCheckTask.network = "tcp";
        portCheckTask.networkMode = "AGENT";
        if (StringUtils.isNotBlank(config.getNetworkMode())) {
          portCheckTask.networkMode = config.getNetworkMode();
        }

        if (StringUtils.isNotBlank(
            MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.NETWORK_MODE))) {
          portCheckTask.networkMode =
              MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.NETWORK_MODE);
        }

        ExecuteRule executeRule = new ExecuteRule();
        executeRule.setType("fixedRate");
        executeRule.setFixedRate(60000);
        portCheckTask.setExecuteRule(executeRule);

        IntegrationTransForm transformMap = new IntegrationTransForm();
        transformMap.setMetricPrefix("portcheck_");
        portCheckTask.setTransform(transformMap);
      }

      {
        portCheckPlugin.tenant = integrationPluginDTO.tenant;
        portCheckPlugin.name = integrationPluginDTO.product.toLowerCase();
        portCheckPlugin.gaeaTableName = integrationPluginDTO.name + "_" + i++;
        portCheckPlugin.collectRange =
            getGaeaCollectRange(integrationPluginDTO, config.range, config.metaLabel);
        portCheckPlugin.portCheckTask = portCheckTask;
        portCheckPlugin.collectPlugin = "dialcheck";

      }

      portCheckPlugins.add(portCheckPlugin);
    }

    return portCheckPlugins;
  }
}
