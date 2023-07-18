/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertNotifyRecordService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.dal.mapper.AlertNotifyRecordMapper;
import io.holoinsight.server.home.dal.converter.AlertNotifyRecordConverter;
import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.NotifyErrorMsg;
import io.holoinsight.server.home.facade.NotifyStage;
import io.holoinsight.server.home.facade.NotifyUser;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AlertNotifyRecordServiceImpl extends
    ServiceImpl<AlertNotifyRecordMapper, AlertNotifyRecord> implements AlertNotifyRecordService {

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Resource
  private AlertNotifyRecordConverter alertNotifyRecordConverter;

  @Override
  public AlertNotifyRecordDTO queryByHistoryDetailId(Long historyDetailId, String tenant,
      String workspace) {
    QueryWrapper<AlertNotifyRecord> wrapper = new QueryWrapper<>();
    this.requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant, workspace);

    wrapper.eq("history_detail_id", historyDetailId);
    wrapper.last("LIMIT 1");
    AlertNotifyRecord alertNotifyRecord = this.getOne(wrapper);
    AlertNotifyRecordDTO alertNotifyRecordDTO =
        alertNotifyRecordConverter.doToDTO(alertNotifyRecord);
    return alertNotifyRecordDTO;
  }

  @Override
  public MonitorPageResult<AlertNotifyRecordDTO> getListByPage(
      MonitorPageRequest<AlertNotifyRecordDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlertNotifyRecord> wrapper = new QueryWrapper<>();

    AlertNotifyRecord alertNotifyRecord =
        alertNotifyRecordConverter.dtoToDO(pageRequest.getTarget());

    if (null != alertNotifyRecord.getId()) {
      wrapper.eq("id", alertNotifyRecord.getId());
    }

    this.requestContextAdapter.queryWrapperTenantAdapt(wrapper, alertNotifyRecord.getTenant(),
        alertNotifyRecord.getWorkspace());

    if (null != alertNotifyRecord.getNotifyErrorTime()) {
      wrapper.like("notify_error_time", alertNotifyRecord.getNotifyErrorTime());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getNotifyChannel())) {
      wrapper.eq("notify_channel", alertNotifyRecord.getNotifyChannel().trim());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getRuleName())) {
      wrapper.like("rule_name", alertNotifyRecord.getRuleName().trim());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getNotifyUser())) {
      wrapper.likeRight("notify_user", alertNotifyRecord.getNotifyUser().trim());
    }

    if (null != alertNotifyRecord.getHistoryId()) {
      wrapper.eq("history_id", alertNotifyRecord.getHistoryId());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getUniqueId())) {
      wrapper.eq("unique_id", alertNotifyRecord.getUniqueId().trim());
    }

    if (null != alertNotifyRecord.getEnvType()) {
      wrapper.eq("env_type", alertNotifyRecord.getEnvType());
    }

    if (Objects.nonNull(alertNotifyRecord.getHistoryDetailId())) {
      wrapper.eq("history_detail_id", alertNotifyRecord.getHistoryDetailId());
    }

    if (Objects.nonNull(alertNotifyRecord.getIsSuccess())) {
      wrapper.eq("is_success", alertNotifyRecord.getIsSuccess());
    }

    wrapper.orderByDesc("id");

    if (null != alertNotifyRecord.getGmtCreate()) {
      wrapper.ge("gmt_create", alertNotifyRecord.getGmtCreate());
    }

    Page<AlertNotifyRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlertNotifyRecordDTO> alertNotifyRecordDTO = new MonitorPageResult<>();

    List<AlertNotifyRecordDTO> alertNotifyRecordDTOList = recordConverter(page.getRecords());


    alertNotifyRecordDTO.setItems(alertNotifyRecordDTOList);
    alertNotifyRecordDTO.setPageNum(pageRequest.getPageNum());
    alertNotifyRecordDTO.setPageSize(pageRequest.getPageSize());
    alertNotifyRecordDTO.setTotalCount(page.getTotal());
    alertNotifyRecordDTO.setTotalPage(page.getPages());

    return alertNotifyRecordDTO;
  }

  private List<AlertNotifyRecordDTO> recordConverter(List<AlertNotifyRecord> records) {
    if (records == null) {
      return null;
    }

    List<AlertNotifyRecordDTO> list = new ArrayList<AlertNotifyRecordDTO>(records.size());
    for (AlertNotifyRecord alertNotifyRecord : records) {
      list.add(dTOConverter(alertNotifyRecord));
    }

    return list;
  }

  private AlertNotifyRecordDTO dTOConverter(AlertNotifyRecord alertNotifyRecord) {
    if (alertNotifyRecord == null) {
      return null;
    }

    AlertNotifyRecordDTO alertNotifyRecordDTO = new AlertNotifyRecordDTO();

    alertNotifyRecordDTO.setId(alertNotifyRecord.getId());
    alertNotifyRecordDTO.setGmtCreate(alertNotifyRecord.getGmtCreate());
    alertNotifyRecordDTO.setGmtModified(alertNotifyRecord.getGmtModified());
    alertNotifyRecordDTO.setHistoryDetailId(alertNotifyRecord.getHistoryDetailId());
    alertNotifyRecordDTO.setHistoryId(alertNotifyRecord.getHistoryId());
    alertNotifyRecordDTO.setNotifyErrorTime(alertNotifyRecord.getNotifyErrorTime());
    alertNotifyRecordDTO.setIsSuccess(alertNotifyRecord.getIsSuccess());
    alertNotifyRecordDTO.setNotifyChannel(alertNotifyRecord.getNotifyChannel());
    alertNotifyRecordDTO.setNotifyUser(alertNotifyRecord.getNotifyUser());
    alertNotifyRecordDTO.setNotifyErrorNode(alertNotifyRecord.getNotifyErrorNode());
    alertNotifyRecordDTO.setTenant(alertNotifyRecord.getTenant());
    alertNotifyRecordDTO.setExtra(alertNotifyRecord.getExtra());
    alertNotifyRecordDTO.setEnvType(alertNotifyRecord.getEnvType());
    alertNotifyRecordDTO.setWorkspace(alertNotifyRecord.getWorkspace());
    alertNotifyRecordDTO.setUniqueId(alertNotifyRecord.getUniqueId());
    alertNotifyRecordDTO.setRuleName(alertNotifyRecord.getRuleName());
    alertNotifyRecordDTO.setTriggerResult(alertNotifyRecord.getTriggerResult());
    alertNotifyRecordDTO
        .setNotifyStage(JSONArray.parseArray(alertNotifyRecord.getExtra(), NotifyStage.class));
    alertNotifyRecordDTO.setNotifyErrorMsgList(
        JSONArray.parseArray(alertNotifyRecord.getNotifyErrorNode(), NotifyErrorMsg.class));
    alertNotifyRecordDTO.setNotifyChannelList(
        JSONArray.parseArray(alertNotifyRecord.getNotifyChannel(), String.class));
    alertNotifyRecordDTO.setNotifyUserList(
        JSONArray.parseArray(alertNotifyRecord.getNotifyUser(), NotifyUser.class));
    return alertNotifyRecordDTO;
  }
}
