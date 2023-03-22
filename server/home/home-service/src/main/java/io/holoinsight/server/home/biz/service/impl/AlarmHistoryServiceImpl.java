/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.AlarmHistoryService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.AlarmHistoryConverter;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AlarmHistoryServiceImpl extends ServiceImpl<AlarmHistoryMapper, AlarmHistory>
    implements AlarmHistoryService {

  @Resource
  private AlarmHistoryConverter alarmHistoryConverter;

  @Override
  public AlarmHistoryDTO queryById(Long id, String tenant, String workspace) {

    QueryWrapper<AlarmHistory> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    AlarmHistory alarmHistory = this.getOne(wrapper);
    return alarmHistoryConverter.doToDTO(alarmHistory);
  }

  @Override
  public MonitorPageResult<AlarmHistoryDTO> getListByPage(
      MonitorPageRequest<AlarmHistoryDTO> pageRequest, List<String> uniqueIds) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmHistory> wrapper = new QueryWrapper<>();

    AlarmHistory alarmHistory = alarmHistoryConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmHistory.getId()) {
      wrapper.eq("id", alarmHistory.getId());
    }

    if (StringUtil.isNotBlank(alarmHistory.getTenant())) {
      wrapper.eq("tenant", alarmHistory.getTenant().trim());
    }

    if (StringUtils.isNotBlank(alarmHistory.getWorkspace())) {
      wrapper.eq("workspace", alarmHistory.getWorkspace());
    }

    if (null != alarmHistory.getUniqueId()) {
      wrapper.eq("unique_id", alarmHistory.getUniqueId());
    }

    if (!CollectionUtils.isEmpty(uniqueIds)) {
      wrapper.in("unique_id", uniqueIds);
    }

    if (StringUtil.isNotBlank(alarmHistory.getRuleName())) {
      wrapper.like("rule_name", alarmHistory.getRuleName().trim());
    }

    if (StringUtil.isNotBlank(alarmHistory.getAlarmLevel())) {
      wrapper.like("alarm_level", alarmHistory.getAlarmLevel().trim());
    }

    if (StringUtil.isNotBlank(alarmHistory.getSourceType())) {
      wrapper.eq("source_type", alarmHistory.getSourceType().trim());
    }

    if (null != alarmHistory.getSourceId()) {
      wrapper.eq("source_id", alarmHistory.getSourceId());
    }

    if (null != alarmHistory.getEnvType()) {
      wrapper.eq("env_type", alarmHistory.getEnvType());
    }

    if (null != alarmHistory.getAlarmTime()) {
      wrapper.ge("alarm_time", alarmHistory.getAlarmTime());
    }

    if (null != pageRequest.getFrom()) {
      wrapper.ge("gmt_create", new Date(pageRequest.getFrom()));
    }

    if (null != pageRequest.getTo()) {
      wrapper.le("gmt_create", new Date(pageRequest.getTo()));
    }

    if (alarmHistory.getRecoverTime() == null) {
      if (alarmHistory.getDuration() != null && alarmHistory.getDuration() == 0) {
        wrapper.isNull("recover_time");
      }
    } else if (alarmHistory.getDuration() != null && alarmHistory.getDuration() == 1) {
      wrapper.isNotNull("recover_time");
    }

    if (StringUtil.isNotBlank(pageRequest.getSortBy())
        && StringUtil.isNotBlank(pageRequest.getSortRule())) {
      if (pageRequest.getSortBy().equals("alarmTime")) {
        if (pageRequest.getSortRule().toLowerCase(Locale.ROOT).equals("desc")) {
          wrapper.orderByDesc("alarm_time");
        } else {
          wrapper.orderByAsc("alarm_time");
        }
      }
    } else {
      // 默认id降序排序
      wrapper.orderByDesc("id");
    }

    Page<AlarmHistory> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmHistoryDTO> alarmHistorys = new MonitorPageResult<>();

    alarmHistorys.setItems(alarmHistoryConverter.dosToDTOs(page.getRecords()));
    alarmHistorys.setPageNum(pageRequest.getPageNum());
    alarmHistorys.setPageSize(pageRequest.getPageSize());
    alarmHistorys.setTotalCount(page.getTotal());
    alarmHistorys.setTotalPage(page.getPages());

    return alarmHistorys;
  }
}
