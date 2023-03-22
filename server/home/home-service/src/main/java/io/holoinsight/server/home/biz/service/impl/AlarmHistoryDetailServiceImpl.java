/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.AlarmHistoryDetailService;
import io.holoinsight.server.home.dal.converter.AlarmHistoryDetailConverter;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryDetailMapper;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AlarmHistoryDetailServiceImpl extends
    ServiceImpl<AlarmHistoryDetailMapper, AlarmHistoryDetail> implements AlarmHistoryDetailService {

  @Resource
  private AlarmHistoryDetailConverter alarmHistoryDetailConverter;

  @Override
  public MonitorPageResult<AlarmHistoryDetailDTO> getListByPage(
      MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmHistoryDetail> wrapper = new QueryWrapper<>();

    AlarmHistoryDetail alarmHistoryDetail =
        alarmHistoryDetailConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmHistoryDetail.getHistoryId()) {
      wrapper.eq("history_id", alarmHistoryDetail.getHistoryId());
    }

    if (null != alarmHistoryDetail.getAlarmTime()) {
      wrapper.eq("alarm_time", alarmHistoryDetail.getAlarmTime());
    }

    if (null != alarmHistoryDetail.getTenant()) {
      wrapper.eq("tenant", alarmHistoryDetail.getTenant());
    }

    if (StringUtils.isNotBlank(alarmHistoryDetail.getWorkspace())) {
      wrapper.eq("workspace", alarmHistoryDetail.getWorkspace());
    }

    if (null != pageRequest.getFrom()) {
      wrapper.ge("alarm_time", new Date(pageRequest.getFrom()));
    }

    if (null != pageRequest.getTo()) {
      wrapper.le("alarm_time", new Date(pageRequest.getTo()));
    }

    if (null != alarmHistoryDetail.getEnvType()) {
      wrapper.eq("env_type", alarmHistoryDetail.getEnvType());
    }

    wrapper.orderByDesc("id");

    Page<AlarmHistoryDetail> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmHistoryDetailDTO> AlarmHistoryDetails = new MonitorPageResult<>();

    AlarmHistoryDetails.setItems(alarmHistoryDetailConverter.dosToDTOs(page.getRecords()));
    AlarmHistoryDetails.setPageNum(pageRequest.getPageNum());
    AlarmHistoryDetails.setPageSize(pageRequest.getPageSize());
    AlarmHistoryDetails.setTotalCount(page.getTotal());
    AlarmHistoryDetails.setTotalPage(page.getPages());

    return AlarmHistoryDetails;
  }
}
