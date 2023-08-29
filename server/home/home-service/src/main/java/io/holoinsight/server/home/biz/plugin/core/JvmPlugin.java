/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.config.BasePluginConfig;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.jvm.JvmTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    String json = integrationPluginDTO.json;

    Map<String, Object> map = J.toMap(json);
    if (!map.containsKey("confs"))
      return jvmPlugins;
    List<BasePluginConfig> basePluginConfigs = J.fromJson(J.toJson(map.get("confs")),
        new TypeToken<List<BasePluginConfig>>() {}.getType());

    int i = 0;
    for (BasePluginConfig basePluginConfig : basePluginConfigs) {
      JvmPlugin jvmPlugin = new JvmPlugin();
      {
        JvmTask jvmTask = new JvmTask();
        jvmTask.setExecuteRule(getExecuteRule());
        jvmTask.setRefMetas(getRefMeta());
        jvmTask.setType(JvmTask.class.getName());

        jvmPlugin.tenant = integrationPluginDTO.tenant;
        jvmPlugin.jvmTask = jvmTask;
        jvmPlugin.name = integrationPluginDTO.product.toLowerCase();
        jvmPlugin.gaeaTableName = integrationPluginDTO.name + "_" + i++;
        jvmPlugin.collectPlugin = JvmTask.class.getName();

        jvmPlugin.collectRange = getGaeaCollectRange(integrationPluginDTO, basePluginConfig.range,
            basePluginConfig.metaLabel);

      }

      jvmPlugins.add(jvmPlugin);
    }
    return jvmPlugins;

  }

}
