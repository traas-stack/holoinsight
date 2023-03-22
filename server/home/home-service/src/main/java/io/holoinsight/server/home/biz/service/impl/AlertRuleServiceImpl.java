/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.AlertBlockService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.dal.converter.AlarmRuleConverter;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.model.AlarmBlock;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:44 上午
 */
@Service
public class AlertRuleServiceImpl extends ServiceImpl<AlarmRuleMapper, AlarmRule>
    implements AlertRuleService {

  @Resource
  private AlarmRuleConverter alarmRuleConverter;

  @Resource
  private AlertBlockService alertBlockService;

  @Override
  public Long save(AlarmRuleDTO alarmRuleDTO) {
    AlarmRule alarmRule = alarmRuleConverter.dtoToDO(alarmRuleDTO);
    this.save(alarmRule);
    return alarmRule.getId();
  }

  @Override
  public Boolean updateById(AlarmRuleDTO alarmRuleDTO) {
    AlarmRule alarmRule = alarmRuleConverter.dtoToDO(alarmRuleDTO);
    return this.updateById(alarmRule);
  }

  @Override
  public AlarmRuleDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmRule> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    AlarmRule alarmRule = this.getOne(wrapper);
    return alarmRuleConverter.doToDTO(alarmRule);
  }

  @Override
  public MonitorPageResult<AlarmRuleDTO> getListByPage(
      MonitorPageRequest<AlarmRuleDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmRule> wrapper = new QueryWrapper<>();

    AlarmRule alarmRule = alarmRuleConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmRule.getId()) {
      wrapper.eq("id", alarmRule.getId());
    }

    if (StringUtils.isNotBlank(alarmRule.getTenant())) {
      wrapper.eq("tenant", alarmRule.getTenant().trim());
    }

    if (StringUtils.isNotBlank(alarmRule.getWorkspace())) {
      wrapper.eq("workspace", alarmRule.getWorkspace());
    }

    if (null != alarmRule.getStatus()) {
      wrapper.like("status", alarmRule.getStatus());
    }

    if (StringUtils.isNotBlank(alarmRule.getAlarmLevel())) {
      wrapper.eq("alarm_level", alarmRule.getAlarmLevel().trim());
    }

    if (StringUtils.isNotBlank(alarmRule.getRuleName())) {
      wrapper.like("rule_name", alarmRule.getRuleName().trim());
    }

    if (StringUtils.isNotBlank(alarmRule.getSourceType())) {
      wrapper.likeRight("source_type", alarmRule.getSourceType().trim());
    }

    if (null != alarmRule.getSourceId()) {
      wrapper.eq("source_id", alarmRule.getSourceId());
    }

    if (StringUtils.isNotBlank(alarmRule.getRuleType())) {
      wrapper.eq("rule_type", alarmRule.getRuleType().trim());
    }

    if (null != alarmRule.getEnvType()) {
      wrapper.eq("env_type", alarmRule.getEnvType());
    }

    if (StringUtils.isNotBlank(alarmRule.getCreator())) {
      wrapper.eq("creator", alarmRule.getCreator());
    }

    if (StringUtils.isNotBlank(alarmRule.getModifier())) {
      wrapper.eq("modifier", alarmRule.getModifier());
    }

    if (StringUtils.isNotBlank(pageRequest.getSortBy())
        && StringUtils.isNotBlank(pageRequest.getSortRule())) {
      if (pageRequest.getSortRule().toLowerCase(Locale.ROOT).equals("desc")) {
        wrapper.orderByDesc(pageRequest.getSortBy());
      } else {
        wrapper.orderByAsc(pageRequest.getSortBy());
      }
    } else {
      wrapper.orderByDesc("gmt_modified");
    }

    if (null != alarmRule.getGmtCreate()) {
      wrapper.ge("gmt_create", alarmRule.getGmtCreate());
    }

    wrapper.select(AlarmRule.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<AlarmRule> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmRuleDTO> alarmRules = new MonitorPageResult<>();

    List<AlarmRuleDTO> alarmRuleList = alarmRuleConverter.dosToDTOs(page.getRecords());

    List<String> alarmUniqueId = alarmRuleList.stream()
        .map(alarmRuleDTO -> alarmRuleDTO.getRuleType() + "_" + alarmRuleDTO.getId())
        .collect(Collectors.toList());

    Map<String, Long> alarmBlockMap = null;
    if (!alarmUniqueId.isEmpty()) {
      QueryWrapper<AlarmBlock> wrapper1 = new QueryWrapper<>();
      wrapper1.in("unique_id", alarmUniqueId);
      wrapper1.ge("end_time", new Date());
      List<AlarmBlock> alarmBlocks = alertBlockService.list(wrapper1);
      alarmBlockMap = alarmBlocks.stream()
          .collect(Collectors.toMap(AlarmBlock::getUniqueId, AlarmBlock::getId, (v1, v2) -> v2));
    }

    Map<String, Long> finalAlarmBlockMap = alarmBlockMap;
    alarmRuleList.parallelStream().forEach(e -> {
      if (e.getRule() != null && e.getRule().containsKey("triggers")) {
        List<String> alarmCOntent = new ArrayList<>();
        List<Map<String, Object>> triggers =
            (List<Map<String, Object>>) e.getRule().get("triggers");
        if (triggers != null) {
          triggers.forEach(map -> {
            if (map.containsKey("triggerContent")) {
              alarmCOntent.add((String) map.get("triggerContent"));
            }
          });
          e.setAlarmContent(alarmCOntent);
        }
      }
      if (finalAlarmBlockMap != null) {
        Long blockId = finalAlarmBlockMap.get(e.getRuleType() + "_" + e.getId());
        e.setBlockId(blockId);
      }
    });

    alarmRules.setItems(alarmRuleList);
    alarmRules.setPageNum(pageRequest.getPageNum());
    alarmRules.setPageSize(pageRequest.getPageSize());
    alarmRules.setTotalCount(page.getTotal());
    alarmRules.setTotalPage(page.getPages());

    return alarmRules;
  }

  @Override
  public List<AlarmRuleDTO> getListByKeyword(String keyword, String tenant, String workspace) {
    QueryWrapper<AlarmRule> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.like("id", keyword).or().like("rule_name", keyword);
    Page<AlarmRule> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return alarmRuleConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<AlarmRuleDTO> findByIds(List<String> ids) {
    QueryWrapper<AlarmRule> wrapper = new QueryWrapper<>();
    wrapper.in("id", ids);
    List<AlarmRule> alarmRules = baseMapper.selectList(wrapper);
    return alarmRuleConverter.dosToDTOs(alarmRules);
  }
}
