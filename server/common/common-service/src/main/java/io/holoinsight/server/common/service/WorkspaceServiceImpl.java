/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.entity.Workspace;
import io.holoinsight.server.common.dao.mapper.WorkspaceMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: WorkspaceServiceImpl.java, v 0.1 2023年03月03日 下午5:59 jsy1001de Exp $
 */
@Service
public class WorkspaceServiceImpl extends ServiceImpl<WorkspaceMapper, Workspace>
    implements WorkspaceService {
  @Override
  public List<Workspace> getByTenant(String tenant) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenant);
    return listByMap(columnMap);
  }
}
