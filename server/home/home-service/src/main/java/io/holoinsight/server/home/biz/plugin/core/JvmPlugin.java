/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.jvm.JvmTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: JvmPlugin.java, v 0.1 2022年11月22日 上午11:14 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.JvmPlugin", version = "1")
public class JvmPlugin extends AbstractLocalIntegrationPlugin<JvmPlugin> {

  public JvmTask jvmTask;

  @Override
  public JvmTask buildTask() {
    return jvmTask;
  }

  @Override
  public List<JvmPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {

    List<JvmPlugin> jvmPlugins = new ArrayList<>();
    JvmPlugin jvmPlugin = new JvmPlugin();
    {
      JvmTask jvmTask =
          J.fromJson(integrationPluginDTO.json, new TypeToken<JvmTask>() {}.getType());

      jvmTask.setExecuteRule(getExecuteRule());
      jvmTask.setRefMetas(getRefMeta());
      jvmTask.setType(JvmTask.class.getName());
      jvmPlugin.jvmTask = jvmTask;
      jvmPlugin.name = integrationPluginDTO.product.toLowerCase();
      jvmPlugin.gaeaTableName = integrationPluginDTO.name;
      GaeaCollectRange gaeaCollectRange =
          J.fromJson(J.toJson(integrationPluginDTO.collectRange), GaeaCollectRange.class);;
      jvmPlugin.collectRange = gaeaCollectRange.cloudmonitor;
      jvmPlugin.collectPlugin = JvmTask.class.getName();
    }

    jvmPlugins.add(jvmPlugin);

    return jvmPlugins;

  }

}
