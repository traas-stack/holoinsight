/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.page.MonitorTimePageRequest;
import io.holoinsight.server.home.dal.mapper.UserOpLogMapper;
import io.holoinsight.server.home.dal.model.UserOpLog;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserOpLogService.java, v 0.1 2022年03月21日 2:55 下午 jinsong.yjs Exp $
 */
@Service
public class UserOpLogServiceImpl extends ServiceImpl<UserOpLogMapper, UserOpLog>
    implements UserOpLogService {

  @Override
  public UserOpLog queryById(Long id, String tenant, String workspace) {
    QueryWrapper<UserOpLog> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    return this.getOne(wrapper);
  }

  public UserOpLog create(UserOpLog userOpLog) {
    userOpLog.setGmtCreate(new Date());
    save(userOpLog);
    return userOpLog;
  }

  public MonitorPageResult<UserOpLog> getListByPage(
      MonitorTimePageRequest<UserOpLog> userOpLogRequest) {

    if (userOpLogRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<UserOpLog> wrapper = new QueryWrapper<>();

    UserOpLog userOpLog = userOpLogRequest.getTarget();

    if (null != userOpLog.getGmtCreate()) {
      wrapper.ge("gmt_create", userOpLog.getGmtCreate());
    }

    if (StringUtil.isNotBlank(userOpLog.getCreator())) {
      wrapper.eq("creator", userOpLog.getCreator().trim());
    }

    if (null != userOpLog.getId()) {
      wrapper.eq("id", userOpLog.getId());
    }

    if (StringUtil.isNotBlank(userOpLog.getName())) {
      wrapper.like("name", userOpLog.getName().trim());
    }

    if (null != userOpLog.getTableName()) {
      wrapper.eq("table_name", userOpLog.getTableName());
    }

    if (null != userOpLog.getTenant()) {
      wrapper.eq("tenant", userOpLog.getTenant());
    }

    if (StringUtils.isNotBlank(userOpLog.getWorkspace())) {
      wrapper.eq("workspace", userOpLog.getWorkspace());
    }

    if (null != userOpLog.getOpType()) {
      wrapper.eq("op_type", userOpLog.getOpType());
    }

    if (null != userOpLog.getTableEntityId()) {
      wrapper.eq("table_entity_id", userOpLog.getTableEntityId());
    }

    wrapper.orderByDesc("gmt_create");


    Page<UserOpLog> page =
        new Page<>(userOpLogRequest.getPageNum(), userOpLogRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<UserOpLog> dtos = new MonitorPageResult<>();

    dtos.setItems(page.getRecords());
    dtos.setPageNum(userOpLogRequest.getPageNum());
    dtos.setPageSize(userOpLogRequest.getPageSize());
    dtos.setTotalCount(page.getTotal());
    dtos.setTotalPage(page.getPages());

    return dtos;

  }

  public UserOpLog append(String tableName, Long tableEntityId, String opType, String user,
      String tenant, String workspace, String before, String after, String relate, String name) {
    UserOpLog opLog = new UserOpLog();
    opLog.setTableName(tableName);
    opLog.setTableEntityId(tableEntityId);
    opLog.setOpType(opType);
    opLog.setCreator(user);
    opLog.setTenant(tenant);
    opLog.setWorkspace(workspace);
    opLog.setGmtCreate(new Date());
    opLog.setGmtCreate(new Date());
    opLog.setOpBeforeContext(before);
    opLog.setOpAfterContext(after);
    opLog.setName(name);
    opLog.setRelate(relate);
    save(opLog);
    EventBusHolder.post(opLog);
    return opLog;
  }

  @Override
  public UserOpLog append(String tableName, String tableEntityUuid, String opType, String user,
      String tenant, String workspace, String before, String after, String relate, String name) {
    UserOpLog opLog = new UserOpLog();
    opLog.setTableName(tableName);
    opLog.setTableEntityUuid(tableEntityUuid);
    opLog.setOpType(opType);
    opLog.setCreator(user);
    opLog.setTenant(tenant);
    opLog.setWorkspace(workspace);
    opLog.setGmtCreate(new Date());
    opLog.setGmtCreate(new Date());
    opLog.setOpBeforeContext(before);
    opLog.setOpAfterContext(after);
    opLog.setName(name);
    opLog.setRelate(relate);
    save(opLog);
    EventBusHolder.post(opLog);
    return opLog;
  }
}
