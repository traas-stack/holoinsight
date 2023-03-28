/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.AlarmGroupConverter;
import io.holoinsight.server.home.dal.mapper.AlarmGroupMapper;
import io.holoinsight.server.home.dal.model.AlarmGroup;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

@Service
public class AlertGroupServiceImpl extends ServiceImpl<AlarmGroupMapper, AlarmGroup>
    implements AlertGroupService {

  @Resource
  private AlarmGroupConverter alarmGroupConverter;

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
    QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
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

    AlarmGroup alarmHistory = alarmGroupConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmHistory.getId()) {
      wrapper.eq("id", alarmHistory.getId());
    }

    if (StringUtils.isNotBlank(alarmHistory.getTenant())) {
      wrapper.eq("tenant", alarmHistory.getTenant().trim());
    }

    if (null != alarmHistory.getEnvType()) {
      wrapper.eq("env_type", alarmHistory.getEnvType());
    }

    if (StringUtils.isNotBlank(alarmHistory.getGroupName())) {
      wrapper.like("group_name", alarmHistory.getGroupName().trim());
    }

    if (StringUtils.isNotBlank(alarmHistory.getCreator())) {
      wrapper.like("creator", alarmHistory.getCreator().trim());
    }
    if (StringUtils.isNotBlank(alarmHistory.getModifier())) {
      wrapper.like("modifior", alarmHistory.getModifier().trim());
    }

    if (StringUtil.isNotBlank(pageRequest.getSortBy())
        && StringUtil.isNotBlank(pageRequest.getSortRule())) {
      if (pageRequest.getSortRule().toLowerCase(Locale.ROOT).equals("desc")) {
        wrapper.orderByDesc(pageRequest.getSortBy());
      } else {
        wrapper.orderByAsc(pageRequest.getSortBy());
      }
    } else {
      wrapper.orderByDesc("gmt_modified");
    }

    wrapper.select(AlarmGroup.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

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
