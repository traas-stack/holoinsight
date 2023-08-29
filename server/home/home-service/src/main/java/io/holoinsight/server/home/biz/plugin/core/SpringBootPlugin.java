/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.config.SpringBootConfig;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.springboot.SpringBootConf;
import io.holoinsight.server.registry.model.integration.springboot.SpringBootTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SpringBootPlugin.java, v 0.1 2022年11月22日 上午11:14 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.SpringBootPlugin", version = "1")
public class SpringBootPlugin extends AbstractLocalIntegrationPlugin<SpringBootPlugin> {

  public SpringBootTask springBootTask;

  @Override
  public SpringBootTask buildTask() {
    return springBootTask;
  }

  @Override
  public List<SpringBootPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {

    List<SpringBootPlugin> springBootPlugins = new ArrayList<>();

    String json = integrationPluginDTO.json;

    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return springBootPlugins;
    List<SpringBootConfig> springBootConfigs = J.fromJson(J.toJson(map.get("confs")),
        new TypeToken<List<SpringBootConfig>>() {}.getType());

    int i = 0;
    for (SpringBootConfig springBootConfig : springBootConfigs) {
      SpringBootPlugin springBootPlugin = new SpringBootPlugin();
      {
        SpringBootTask springBootTask = new SpringBootTask();
        {
          SpringBootConf springBootConf = new SpringBootConf();
          springBootConf.setPort(Integer.parseInt(springBootConfig.getPort()));
          springBootConf.setBaseUrl(springBootConfig.getBaseUrl());
          springBootTask.setConf(springBootConf);
          springBootTask.setName(integrationPluginDTO.getProduct());
          springBootTask.setType(SpringBootTask.class.getName());
          springBootTask.setExecuteRule(getExecuteRule());
          springBootTask.setRefMetas(getRefMeta());
        }
        springBootPlugin.springBootTask = springBootTask;
        springBootPlugin.tenant = integrationPluginDTO.tenant;
        springBootPlugin.name = integrationPluginDTO.product.toLowerCase();
        springBootPlugin.gaeaTableName = integrationPluginDTO.name + "_" + i++;

        springBootPlugin.collectRange = getGaeaCollectRange(integrationPluginDTO,
            springBootConfig.range, springBootConfig.metaLabel);

        springBootPlugin.collectPlugin = SpringBootTask.class.getName();
      }

      springBootPlugins.add(springBootPlugin);
    }

    return springBootPlugins;
  }

}
