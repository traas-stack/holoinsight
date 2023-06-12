/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.UserFavoriteService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.mapper.UserFavoriteMapper;
import io.holoinsight.server.home.dal.model.UserFavorite;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserFavoriteServiceImpl.java, v 0.1 2022年10月20日 上午11:31 jinsong.yjs Exp $
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
    implements UserFavoriteService {

  @Override
  public UserFavorite queryById(Long id, String tenant, String workspace) {
    QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.last("LIMIT 1");

    return this.getOne(wrapper);
  }

  public List<UserFavorite> getByUser(String userLoginName) {

    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("user_login_name", userLoginName);
    return listByMap(columnMap);
  }

  public List<UserFavorite> getByUserAndTenant(String userLoginName, String tenant,
      String workspace) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("user_login_name", userLoginName);
    columnMap.put("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      columnMap.put("workspace", workspace);
    }
    return listByMap(columnMap);
  }

  public List<UserFavorite> getByUserAndTenantAndRelateId(String userLoginName, String tenant,
      String workspace, String relateId, String type) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("user_login_name", userLoginName);
    columnMap.put("tenant", tenant);
    columnMap.put("type", type);
    columnMap.put("relate_id", relateId);
    if (StringUtils.isNotBlank(workspace)) {
      columnMap.put("workspace", workspace);
    }
    return listByMap(columnMap);
  }

  public List<UserFavorite> getByUserAndTenantAndRelateIds(String userLoginName, String tenant,
      String workspace, List<String> relateIds, String type) {

    QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
    wrapper.eq("user_login_name", userLoginName);
    wrapper.eq("tenant", tenant);
    wrapper.eq("type", type);
    wrapper.select().in("relate_id", relateIds);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    return baseMapper.selectList(wrapper);
  }

  public UserFavorite create(UserFavorite userFavorite) {
    userFavorite.setGmtCreate(new Date());
    userFavorite.setGmtModified(new Date());
    save(userFavorite);
    return userFavorite;
  }

  @Override
  public void deleteById(Long id) {
    removeById(id);
  }

  public void update(UserFavorite userFavorite) {
    userFavorite.setGmtModified(new Date());
    updateById(userFavorite);
  }

  public MonitorPageResult<UserFavorite> getListByPage(
      MonitorPageRequest<UserFavorite> userFavoriteRequest) {

    if (userFavoriteRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();

    UserFavorite userFavorite = userFavoriteRequest.getTarget();

    if (null != userFavorite.getGmtCreate()) {
      wrapper.ge("gmt_create", userFavorite.getGmtCreate());
    }
    if (null != userFavorite.getGmtModified()) {
      wrapper.le("gmt_modified", userFavorite.getGmtCreate());
    }

    if (StringUtil.isNotBlank(userFavorite.getUserLoginName())) {
      wrapper.eq("user_login_name", userFavorite.getUserLoginName().trim());
    }

    if (null != userFavorite.getId()) {
      wrapper.eq("id", userFavorite.getId());
    }

    if (StringUtil.isNotBlank(userFavorite.getName())) {
      wrapper.like("name", userFavorite.getName().trim());
    }

    if (StringUtil.isNotBlank(userFavorite.getType())) {
      wrapper.like("type", userFavorite.getType().trim());
    }

    if (null != userFavorite.getRelateId()) {
      wrapper.eq("relate_id", userFavorite.getRelateId());
    }

    if (null != userFavorite.getTenant()) {
      wrapper.eq("tenant", userFavorite.getTenant());
    }

    if (null != userFavorite.getWorkspace()) {
      wrapper.eq("workspace", userFavorite.getWorkspace());
    }

    wrapper.orderByDesc("id");

    wrapper.select(UserFavorite.class, info -> !info.getColumn().equals("user_login_name"));

    Page<UserFavorite> page =
        new Page<>(userFavoriteRequest.getPageNum(), userFavoriteRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<UserFavorite> dtos = new MonitorPageResult<>();

    dtos.setItems(page.getRecords());
    dtos.setPageNum(userFavoriteRequest.getPageNum());
    dtos.setPageSize(userFavoriteRequest.getPageSize());
    dtos.setTotalCount(page.getTotal());
    dtos.setTotalPage(page.getPages());

    return dtos;

  }
}
