/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.IntegrationConfig;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.registry.model.integration.ob.ObTask;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: PortCheckPlugin.java, v 0.1 2022年11月18日 下午5:56 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.OceanBasePlugin", version = "1")
public class OceanBasePlugin extends AbstractCentralIntegrationPlugin<OceanBasePlugin> {

  public ObTask obTask;

  @Override
  ObTask buildTask() {
    return obTask;
  }

  @Override
  public List<OceanBasePlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {
    List<OceanBasePlugin> oceanBasePlugins = new ArrayList<>();

    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("version", integrationPluginDTO.version);
    columnMap.put("name", integrationPluginDTO.product);
    List<IntegrationProductDTO> byMap = integrationProductService.findByMap(columnMap);
    if (CollectionUtils.isEmpty(byMap))
      return oceanBasePlugins;

    IntegrationProductDTO productDTO = byMap.get(0);
    List<IntegrationConfig> configList = productDTO.getForm().getConfigList();

    for (IntegrationConfig config : configList) {


      OceanBasePlugin oceanBasePlugin = new OceanBasePlugin();

      ObTask obTask = J.fromJson(J.toJson(config.getConf()), new TypeToken<ObTask>() {}.getType());

      {
        oceanBasePlugin.tenant = integrationPluginDTO.tenant;
        oceanBasePlugin.metricName = String.join("_", ANTGROUP_METRIC_PREFIX,
            integrationPluginDTO.product.toLowerCase(), config.name);

        oceanBasePlugin.collectRange = GaeaConvertUtil.convertCloudMonitorRange("ob_node_tenant",
            MetaLabel.allApp, new ArrayList<>());
        oceanBasePlugin.gaeaTableName = integrationPluginDTO.name + "_" + config.name;

        oceanBasePlugin.obTask = obTask;
        oceanBasePlugin.collectPlugin = "obcollector";
      }

      oceanBasePlugins.add(oceanBasePlugin);
    }

    return oceanBasePlugins;
  }

}
