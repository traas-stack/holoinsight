/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertNotifyRecordService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.dal.converter.AlertNotifyRecordConverter;
import io.holoinsight.server.home.dal.mapper.AlertNotifyRecordMapper;
import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AlertNotifyRecordServiceImpl extends
    ServiceImpl<AlertNotifyRecordMapper, AlertNotifyRecord> implements AlertNotifyRecordService {

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Autowired
  private AlertNotifyRecordConverter alertNotifyRecordConverter;

  @Override
  public AlertNotifyRecordDTO queryByHistoryDetailId(Long historyDetailId, String tenant,
      String workspace) {
    QueryWrapper<AlertNotifyRecord> wrapper = new QueryWrapper<>();
    this.requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant, workspace);

    wrapper.eq("history_detail_id", historyDetailId);
    wrapper.last("LIMIT 1");
    AlertNotifyRecord alertNotifyRecord = this.getOne(wrapper);
    return alertNotifyRecordConverter.doToDTO(alertNotifyRecord);
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
      wrapper.ge("notify_error_time", alertNotifyRecord.getNotifyErrorTime());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getNotifyChannel())) {
      wrapper.eq("notify_channel", alertNotifyRecord.getNotifyChannel().trim());
    }

    if (StringUtils.isNotBlank(alertNotifyRecord.getRuleName())) {
      wrapper.like("rule_name", alertNotifyRecord.getRuleName().trim());
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

    MonitorPageResult<AlertNotifyRecordDTO> pageResult = new MonitorPageResult<>();

    List<AlertNotifyRecordDTO> alertNotifyRecordDTOList =
        alertNotifyRecordConverter.dosToDTOs(page.getRecords());

    pageResult.setItems(alertNotifyRecordDTOList);
    pageResult.setPageNum(pageRequest.getPageNum());
    pageResult.setPageSize(pageRequest.getPageSize());
    pageResult.setTotalCount(page.getTotal());
    pageResult.setTotalPage(page.getPages());

    return pageResult;
  }

  @Override
  public void insert(AlertNotifyRecordDTO alertNotifyRecordDTO) {
    AlertNotifyRecord alertNotifyRecord = alertNotifyRecordConverter.dtoToDO(alertNotifyRecordDTO);

    this.save(alertNotifyRecord);
  }
}
