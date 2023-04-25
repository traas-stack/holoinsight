/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.mysql.MysqlTask;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MysqlPlugin.java, v 0.1 2022年11月22日 上午11:14 jinsong.yjs Exp $
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.MysqlPlugin", version = "1")
public class MysqlPlugin extends AbstractCentralIntegrationPlugin<MysqlPlugin> {

  public MysqlTask mysqlTask;

  @Override
  public MysqlTask buildTask() {
    return mysqlTask;
  }

  @Override
  public List<MysqlPlugin> genPluginList(IntegrationPluginDTO integrationPluginDTO) {

    List<MysqlPlugin> mysqlPlugins = new ArrayList<>();
    MysqlPlugin mysqlPlugin = new MysqlPlugin();
    {
      MysqlTask mysqlTask =
          J.fromJson(integrationPluginDTO.json, new TypeToken<MysqlTask>() {}.getType());

      mysqlTask.setExecuteRule(getExecuteRule());

      mysqlPlugin.mysqlTask = mysqlTask;
      mysqlPlugin.gaeaTableName = integrationPluginDTO.name;
      mysqlPlugin.collectPlugin = MysqlTask.class.getName();
    }

    mysqlPlugins.add(mysqlPlugin);

    return mysqlPlugins;

  }
}
