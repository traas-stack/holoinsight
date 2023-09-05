/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertBlockService;
import io.holoinsight.server.home.dal.converter.AlarmBlockConverter;
import io.holoinsight.server.home.dal.mapper.AlarmBlockMapper;
import io.holoinsight.server.home.dal.model.AlarmBlock;
import io.holoinsight.server.home.dal.model.dto.AlarmBlockDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:44 上午
 */
@Service
public class AlertBlockServiceImpl extends ServiceImpl<AlarmBlockMapper, AlarmBlock>
    implements AlertBlockService {

  @Resource
  private AlarmBlockConverter alarmBlockConverter;

  @Override
  public Long save(AlarmBlockDTO alarmBlockDTO) {
    AlarmBlock alarmBlock = alarmBlockConverter.dtoToDO(alarmBlockDTO);
    long currentTime = System.currentTimeMillis();
    long endTime =
        currentTime + alarmBlock.getHour() * 1000 * 3600 + alarmBlock.getMinute() * 1000 * 60;
    alarmBlock.setStartTime(new Date(currentTime));
    alarmBlock.setEndTime(new Date(endTime));
    this.save(alarmBlock);
    return alarmBlock.getId();
  }

  @Override
  public Boolean updateById(AlarmBlockDTO alarmBlockDTO) {
    AlarmBlock alarmBlock = alarmBlockConverter.dtoToDO(alarmBlockDTO);
    return this.updateById(alarmBlock);
  }

  @Override
  public AlarmBlockDTO queryById(Long id, String tenant, String workspace) {

    QueryWrapper<AlarmBlock> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    AlarmBlock alarmBlock = this.getOne(wrapper);
    return alarmBlockConverter.doToDTO(alarmBlock);
  }

  @Override
  public AlarmBlockDTO queryByRuleId(String uniqueId, String tenant, String workspace) {
    QueryWrapper<AlarmBlock> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("unique_id", uniqueId);
    wrapper.ge("end_time", new Date());
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    AlarmBlock alarmBlock = this.getOne(wrapper);
    return alarmBlockConverter.doToDTO(alarmBlock);
  }

  @Override
  public MonitorPageResult<AlarmBlockDTO> getListByPage(
      MonitorPageRequest<AlarmBlockDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmBlock> wrapper = new QueryWrapper<>();

    AlarmBlock alarmBlock = alarmBlockConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmBlock.getId()) {
      wrapper.eq("id", alarmBlock.getId());
    }

    if (StringUtils.isNotBlank(alarmBlock.getTenant())) {
      wrapper.eq("tenant", alarmBlock.getTenant().trim());
    }

    if (StringUtils.isNotBlank(alarmBlock.getWorkspace())) {
      wrapper.eq("workspace", alarmBlock.getWorkspace());
    }

    wrapper.orderByDesc("id");

    wrapper.select(AlarmBlock.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<AlarmBlock> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmBlockDTO> alarmBlocks = new MonitorPageResult<>();

    List<AlarmBlockDTO> alarmBlockList = alarmBlockConverter.dosToDTOs(page.getRecords());

    alarmBlocks.setItems(alarmBlockList);
    alarmBlocks.setPageNum(pageRequest.getPageNum());
    alarmBlocks.setPageSize(pageRequest.getPageSize());
    alarmBlocks.setTotalCount(page.getTotal());
    alarmBlocks.setTotalPage(page.getPages());

    return alarmBlocks;
  }

  @Override
  public List<AlarmBlockDTO> getListByKeyword(String keyword, String tenant, String workspace) {
    QueryWrapper<AlarmBlock> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.like("id", keyword).or().like("rule_name", keyword);
    Page<AlarmBlock> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return alarmBlockConverter.dosToDTOs(page.getRecords());
  }
}
