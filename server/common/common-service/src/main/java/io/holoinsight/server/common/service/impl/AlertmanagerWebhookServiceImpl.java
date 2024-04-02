/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import io.holoinsight.server.common.dao.mapper.AlertmanagerWebhookMapper;
import io.holoinsight.server.common.dao.entity.AlertmanagerWebhook;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.service.AlertmanagerWebhookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class AlertmanagerWebhookServiceImpl
    extends ServiceImpl<AlertmanagerWebhookMapper, AlertmanagerWebhook>
    implements AlertmanagerWebhookService {
  @Override
  public MonitorPageResult<AlertmanagerWebhook> getListByPage(
      MonitorPageRequest<AlertmanagerWebhook> request) {
    if (request.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlertmanagerWebhook> wrapper = new QueryWrapper<>();

    AlertmanagerWebhook alertmanagerWebhook = request.getTarget();

    if (null != alertmanagerWebhook.getGmtCreate()) {
      wrapper.ge("gmt_create", alertmanagerWebhook.getGmtCreate());
    }
    if (null != alertmanagerWebhook.getGmtModified()) {
      wrapper.le("gmt_modified", alertmanagerWebhook.getGmtCreate());
    }

    if (StringUtils.isNotBlank(alertmanagerWebhook.getCreator())) {
      wrapper.eq("creator", alertmanagerWebhook.getCreator().trim());
    }

    if (StringUtils.isNotBlank(alertmanagerWebhook.getModifier())) {
      wrapper.eq("modifier", alertmanagerWebhook.getModifier().trim());
    }

    if (null != alertmanagerWebhook.getId()) {
      wrapper.eq("id", alertmanagerWebhook.getId());
    }

    if (StringUtils.isNotBlank(alertmanagerWebhook.getTenant())) {
      wrapper.eq("tenant", alertmanagerWebhook.getTenant().trim());
    }

    if (StringUtils.isNotBlank(alertmanagerWebhook.getName())) {
      wrapper.like("name", alertmanagerWebhook.getName().trim());
    }

    wrapper.orderByDesc("id");

    wrapper.select(AlertmanagerWebhook.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));
    Page<AlertmanagerWebhook> page = new Page<>(request.getPageNum(), request.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlertmanagerWebhook> monitorPageResult = new MonitorPageResult<>();

    monitorPageResult.setItems(page.getRecords());
    monitorPageResult.setPageNum(request.getPageNum());
    monitorPageResult.setPageSize(request.getPageSize());
    monitorPageResult.setTotalCount(page.getTotal());
    monitorPageResult.setTotalPage(page.getPages());

    return monitorPageResult;
  }

  @Override
  public AlertmanagerWebhook queryById(Long id, String tenant) {
    QueryWrapper<AlertmanagerWebhook> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    return this.getOne(wrapper);
  }
}

