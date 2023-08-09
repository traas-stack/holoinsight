/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.DashboardService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.mapper.DashboardMapper;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard>
    implements DashboardService {

  @Override
  public Dashboard queryById(Long id, String tenant, String workspace) {
    QueryWrapper<Dashboard> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    Dashboard model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }
    return model;
  }

  @Override
  public Dashboard queryById(Long id, String tenant) {
    return queryById(id, tenant, null);
  }


  @Override
  public List<Dashboard> findByIds(List<String> ids) {
    QueryWrapper<Dashboard> wrapper = new QueryWrapper<>();
    wrapper.in("id", ids);
    return baseMapper.selectList(wrapper);
  }

  @Override
  public List<Dashboard> getListByKeyword(String keyword, String tenant, String workspace) {
    QueryWrapper<Dashboard> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtil.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.and(wa -> wa.like("id", keyword).or().like("title", keyword));
    wrapper.last("LIMIT 10");
    // Page<Dashboard> page = new Page<>(1, 20);
    // page = page(page, wrapper);

    return baseMapper.selectList(wrapper);
  }

  @Override
  public MonitorPageResult<Dashboard> getListByPage(MonitorPageRequest<Dashboard> request) {
    if (request.getTarget() == null) {
      return null;
    }

    QueryWrapper<Dashboard> wrapper = new QueryWrapper<>();

    Dashboard dashboard = request.getTarget();

    if (null != dashboard.getGmtCreate()) {
      wrapper.ge("gmt_create", dashboard.getGmtCreate());
    }
    if (null != dashboard.getGmtModified()) {
      wrapper.le("gmt_modified", dashboard.getGmtCreate());
    }

    if (StringUtil.isNotBlank(dashboard.getCreator())) {
      wrapper.eq("creator", dashboard.getCreator().trim());
    }

    if (StringUtil.isNotBlank(dashboard.getModifier())) {
      wrapper.eq("modifier", dashboard.getModifier().trim());
    }

    if (null != dashboard.getId()) {
      wrapper.eq("id", dashboard.getId());
    }

    if (null != dashboard.getType()) {
      wrapper.eq("type", dashboard.getType());
    }

    if (StringUtil.isNotBlank(dashboard.getTenant())) {
      wrapper.eq("tenant", dashboard.getTenant().trim());
    }

    if (StringUtil.isNotBlank(dashboard.getWorkspace())) {
      wrapper.eq("workspace", dashboard.getWorkspace().trim());
    }

    if (StringUtil.isNotBlank(dashboard.getTitle())) {
      wrapper.like("title", dashboard.getTitle().trim());
    }

    wrapper.orderByDesc("id");

    Page<Dashboard> page = new Page<>(request.getPageNum(), request.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<Dashboard> dashboardMonitorPageResult = new MonitorPageResult<>();

    dashboardMonitorPageResult.setItems(page.getRecords());
    dashboardMonitorPageResult.setPageNum(request.getPageNum());
    dashboardMonitorPageResult.setPageSize(request.getPageSize());
    dashboardMonitorPageResult.setTotalCount(page.getTotal());
    dashboardMonitorPageResult.setTotalPage(page.getPages());

    return dashboardMonitorPageResult;
  }

  @Override
  public Long create(Dashboard dashboard) {
    this.save(dashboard);
    return dashboard.getId();
  }
}
