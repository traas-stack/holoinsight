/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.entity.MonitorInstance;
import io.holoinsight.server.common.dao.mapper.MonitorInstanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitorInstanceServiceImpl extends ServiceImpl<MonitorInstanceMapper, MonitorInstance>
    implements MonitorInstanceService {

  @Override
  public List<MonitorInstance> listValid() {
    Map<String, Object> cols = new HashMap<>();
    cols.put("deleted", 0);
    return listByMap(cols);
  }

  @Override
  public List<MonitorInstance> queryByInstance(String instance) {
    Map<String, Object> cols = new HashMap<>();
    cols.put("instance", instance);
    cols.put("deleted", 0);
    return listByMap(cols);
  }

  @Override
  public List<MonitorInstance> queryByWorkspace(String workspace) {
    Map<String, Object> cols = new HashMap<>();
    cols.put("workspace", workspace);
    cols.put("deleted", 0);
    return listByMap(cols);
  }

  @Override
  public List<MonitorInstance> queryByType(String type) {
    Map<String, Object> cols = new HashMap<>();
    cols.put("type", type);
    cols.put("deleted", 0);
    return listByMap(cols);
  }

  @Override
  public MonitorInstance queryByInstanceAndType(String instance, String type) {
    Map<String, Object> cols = new HashMap<>();
    cols.put("instance", instance);
    cols.put("type", type);
    cols.put("deleted", 0);
    List<MonitorInstance> instances = listByMap(cols);
    if (CollectionUtils.isEmpty(instances)) {
      return null;
    }
    return instances.get(0);
  }

  @Override
  public Long insert(MonitorInstance monitorInstance) {
    this.save(monitorInstance);
    return monitorInstance.getId();
  }

  @Override
  public Boolean updateByInstanceAndType(MonitorInstance monitorInstance) {
    UpdateWrapper<MonitorInstance> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("instance", monitorInstance.getInstance());
    updateWrapper.eq("type", monitorInstance.getType());
    return update(monitorInstance, updateWrapper);
  }

  @Override
  public Boolean updateByInstance(MonitorInstance monitorInstance) {
    UpdateWrapper<MonitorInstance> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("instance", monitorInstance.getInstance());
    return update(monitorInstance, updateWrapper);
  }

}
