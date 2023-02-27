/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.springboot.SpringBootTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: SpringBootPlugin.java, v 0.1 2022年11月22日 上午11:14 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin..SpringBootPlugin", version = "1")
public class SpringBootPlugin extends AbstractLocalIntegrationPlugin<SpringBootPlugin> {

  public SpringBootTask springBootTask;

  @Override
  SpringBootTask buildTask() {
    return springBootTask;
  }

  @Override
  public List<SpringBootPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {

    List<SpringBootPlugin> springBootPlugins = new ArrayList<>();
    SpringBootPlugin springBootPlugin = new SpringBootPlugin();
    {
      SpringBootTask springBootTask =
          J.fromJson(integrationPluginDTO.json, new TypeToken<SpringBootTask>() {}.getType());

      springBootTask.setExecuteRule(getExecuteRule());

      springBootTask.setRefMetas(getRefMeta());

      springBootPlugin.springBootTask = springBootTask;
      springBootPlugin.gaeaTableName = integrationPluginDTO.name;
      GaeaCollectRange gaeaCollectRange =
          J.fromJson(J.toJson(integrationPluginDTO.collectRange), GaeaCollectRange.class);
      springBootPlugin.collectRange = gaeaCollectRange.cloudmonitor;
      springBootPlugin.collectPlugin = SpringBootTask.class.getName();
    }

    springBootPlugins.add(springBootPlugin);

    return springBootPlugins;

  }

}
