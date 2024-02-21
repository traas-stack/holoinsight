/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.MonitorInstance;

import java.util.List;

public interface MonitorInstanceService extends IService<MonitorInstance> {

  List<MonitorInstance> listValid();

  List<MonitorInstance> queryByInstance(String instance);

  List<MonitorInstance> queryByWorkspace(String workspace);

  List<MonitorInstance> queryByType(String type);

  MonitorInstance queryByInstanceAndType(String instance, String type);

  Long insert(MonitorInstance specInstanceState);

  Boolean updateByInstanceAndType(MonitorInstance monitorInstance);

  Boolean updateByInstance(MonitorInstance monitorInstance);

}
