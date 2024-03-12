/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.dal.converter.AlarmGroupConverter;
import io.holoinsight.server.home.dal.mapper.AlarmGroupMapper;
import io.holoinsight.server.home.dal.model.AlarmGroup;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AlertGroupServiceImpl extends ServiceImpl<AlarmGroupMapper, AlarmGroup>
    implements AlertGroupService {

  @Resource
  private AlarmGroupConverter alarmGroupConverter;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public Long save(AlarmGroupDTO alarmGroupDTO) {
    AlarmGroup alarmGroup = alarmGroupConverter.dtoToDO(alarmGroupDTO);
    this.save(alarmGroup);
    return alarmGroup.getId();
  }

  @Override
  public Boolean updateById(AlarmGroupDTO alarmGroupDTO) {
    AlarmGroup alarmGroup = alarmGroupConverter.dtoToDO(alarmGroupDTO);
    return this.updateById(alarmGroup);
  }

  @Override
  public AlarmGroupDTO queryById(Long id, String tenant) {
    return queryById(id, tenant, requestContextAdapter.getWorkspace(true));
  }

  @Override
  public AlarmGroupDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();
    requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant, workspace);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    AlarmGroup alarmGroup = this.getOne(wrapper);
    return alarmGroupConverter.doToDTO(alarmGroup);
  }

  @Override
  public List<AlarmGroupDTO> getListByUserLike(String userId, String tenant) {
    QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.select().like("group_info", userId);
    List<AlarmGroup> alarmGroups = baseMapper.selectList(wrapper);
    return alarmGroupConverter.dosToDTOs(alarmGroups);
  }

  @Override
  public MonitorPageResult<AlarmGroupDTO> getListByPage(
      MonitorPageRequest<AlarmGroupDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();

    AlarmGroup alarmGroup = alarmGroupConverter.dtoToDO(pageRequest.getTarget());

    this.requestContextAdapter.queryWrapperTenantAdapt(wrapper, alarmGroup.getTenant(),
        alarmGroup.getWorkspace());

    if (null != alarmGroup.getId()) {
      wrapper.eq("id", alarmGroup.getId());
    }

    if (null != alarmGroup.getEnvType()) {
      wrapper.eq("env_type", alarmGroup.getEnvType());
    }

    if (StringUtils.isNotBlank(alarmGroup.getGroupName())) {
      wrapper.like("group_name", alarmGroup.getGroupName().trim());
    }

    if (StringUtils.isNotBlank(alarmGroup.getCreator())) {
      wrapper.like("creator", alarmGroup.getCreator().trim());
    }
    if (StringUtils.isNotBlank(alarmGroup.getModifier())) {
      wrapper.like("modifier", alarmGroup.getModifier().trim());
    }

    wrapper.orderByDesc("id");

    Page<AlarmGroup> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmGroupDTO> pageResult = new MonitorPageResult<>();

    pageResult.setItems(alarmGroupConverter.dosToDTOs(page.getRecords()));
    pageResult.setPageNum(pageRequest.getPageNum());
    pageResult.setPageSize(pageRequest.getPageSize());
    pageResult.setTotalCount(page.getTotal());
    pageResult.setTotalPage(page.getPages());

    return pageResult;
  }
}
