/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.converter.DoConvert;
import io.holoinsight.server.home.alert.service.data.CacheData;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.LogPattern;
import io.holoinsight.server.home.facade.InspectConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:35 下午
 */
@Service
public class CacheAlertTask {

  private static Logger LOGGER = LoggerFactory.getLogger(CacheAlertTask.class);

  private static final ScheduledExecutorService syncExecutorService =
      new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "CacheAlarmTaskScheduled"));

  private static final Integer LIMIT = 5000;
  protected final AtomicInteger rulePageSize = new AtomicInteger();
  protected final AtomicInteger rulePageNum = new AtomicInteger();
  protected final AtomicInteger aiPageSize = new AtomicInteger();
  protected final AtomicInteger aiPageNum = new AtomicInteger();
  protected final AtomicInteger pqlPageSize = new AtomicInteger();
  protected final AtomicInteger pqlPageNum = new AtomicInteger();

  @Resource
  protected AlarmRuleMapper alarmRuleDOMapper;

  @Resource
  private CacheData cacheData;

  @Autowired
  private CacheAlertConfig cacheAlertConfig;

  @Autowired
  private CustomPluginService customPluginService;

  public void start() {
    LOGGER.info("[AlarmConfig] start alarm config syn!");

    syncExecutorService.scheduleAtFixedRate(this::getAlarmTaskCache, 0, 60, TimeUnit.SECONDS);
    LOGGER.info("[AlarmConfig] alarm config sync finish!");
  }

  private void getAlarmTaskCache() {
    try {
      if (!"false".equals(this.cacheAlertConfig.getConfig("alarm_switch"))) {
        // Get alert detection tasks
        List<AlarmRule> alarmRuleDOS = getAlarmRuleListByPage();
        ComputeTaskPackage computeTaskPackage = convert(alarmRuleDOS);
        TaskQueueManager.getInstance().offer(computeTaskPackage);
      }

    } catch (Exception e) {
      LOGGER.error("InspectCtlParam Sync Exception", e);
    }
  }

  public ComputeTaskPackage convert(List<AlarmRule> alarmRuleDOS) {
    ComputeTaskPackage computeTaskPackage = new ComputeTaskPackage();
    computeTaskPackage.setTraceId(UUID.randomUUID().toString());
    List<InspectConfig> inspectConfigs = new ArrayList<>();
    Map<String, List<InspectConfig>> logInspectConfigs = new HashMap<>();
    Map<String, InspectConfig> uniqueIdMap = new HashMap<>();
    try {
      alarmRuleDOS.forEach(alarmRuleDO -> {
        InspectConfig inspectConfig = DoConvert.alarmRuleConverter(alarmRuleDO);
        if (enableAlert(inspectConfig)) {
          // cache
          if (isLogAlert(inspectConfig)) {
            List<InspectConfig> configs = logInspectConfigs.computeIfAbsent(
                String.valueOf(inspectConfig.getSourceId()), k -> new ArrayList<>());
            configs.add(inspectConfig);
          }
          uniqueIdMap.put(inspectConfig.getUniqueId(), inspectConfig);
          inspectConfigs.add(inspectConfig);
        }
      });
      supplementLogConfig(logInspectConfigs);
      cacheData.setUniqueIdMap(uniqueIdMap);
      if (inspectConfigs.size() != 0) {
        computeTaskPackage.setInspectConfigs(inspectConfigs);
      }
    } catch (Exception e) {
      LOGGER.error("fail to convert alarmRules for {}", e.getMessage(), e);
    }
    return computeTaskPackage;
  }

  private void supplementLogConfig(Map<String, List<InspectConfig>> logInspectConfigs) {
    if (CollectionUtils.isEmpty(logInspectConfigs)) {
      return;
    }
    List<CustomPluginDTO> cps =
        this.customPluginService.findByIds(new ArrayList<>(logInspectConfigs.keySet()));
    if (CollectionUtils.isEmpty(cps)) {
      return;
    }
    for (CustomPluginDTO customPluginDTO : cps) {
      CustomPluginConf conf = customPluginDTO.getConf();
      if (conf == null || conf.logParse == null || conf.logParse.pattern == null) {
        continue;
      }
      LogPattern pattern = conf.logParse.pattern;
      if (pattern.logPattern != null && pattern.logPattern) {
        List<InspectConfig> configs = logInspectConfigs.get(String.valueOf(customPluginDTO.id));
        for (InspectConfig inspectConfig : configs) {
          inspectConfig.setLogPatternEnable(true);
        }
      }
    }
  }

  private boolean isLogAlert(InspectConfig inspectConfig) {
    return StringUtils.isNotEmpty(inspectConfig.getSourceType())
        && inspectConfig.getSourceType().equals("log") && inspectConfig.getSourceId() != null
        && inspectConfig.getSourceId() > 0;
  }

  private boolean enableAlert(InspectConfig inspectConfig) {
    Boolean status = inspectConfig.getStatus();
    return status != null && status;
  }

  public Integer ruleSize(String ruleType) {
    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("rule_type", ruleType);
    return this.alarmRuleDOMapper.selectCount(queryWrapper).intValue();
  }

  protected List<AlarmRule> getAlarmRuleListByPage() {
    List<AlarmRule> alarmRules = new ArrayList<>();
    List<AlarmRule> ruleAlerts =
        getAlertRule("rule", this.rulePageNum.get(), this.rulePageSize.get());
    List<AlarmRule> aiAlerts = getAlertRule("ai", this.aiPageNum.get(), this.aiPageSize.get());
    List<AlarmRule> pqlAlerts = getAlertRule("pql", this.pqlPageNum.get(), this.pqlPageSize.get());
    if (!CollectionUtils.isEmpty(ruleAlerts)) {
      alarmRules.addAll(ruleAlerts);
    }
    if (!CollectionUtils.isEmpty(aiAlerts)) {
      alarmRules.addAll(aiAlerts);
    }
    if (!CollectionUtils.isEmpty(pqlAlerts)) {
      alarmRules.addAll(pqlAlerts);
    }
    return alarmRules;
  }

  private List<AlarmRule> getAlertRule(String ruleType, int pageNum, int pageSize) {
    List<AlarmRule> alarmRuleDOS = new ArrayList<>();
    int limit;
    int offset;
    if (pageSize > 0) {
      limit = pageSize;
      offset = pageNum;
    } else {
      return Collections.emptyList();
    }
    QueryWrapper<AlarmRule> condition = new QueryWrapper<>();
    condition.orderByDesc("id");
    condition.eq("rule_type", ruleType);
    condition.last("limit " + limit + " offset " + offset);
    List<AlarmRule> alarmRuleDo = alarmRuleDOMapper.selectList(condition);
    if (!CollectionUtils.isEmpty(alarmRuleDo)) {
      alarmRuleDOS.addAll(alarmRuleDo);
    }
    LOGGER.info("rule_type={}, limit={}, offset={}, size={}", ruleType, limit, offset,
        alarmRuleDOS.size());
    return alarmRuleDOS;
  }

  public void setRulePageSize(int size) {
    this.rulePageSize.set(size);
  }

  public void setRulePageNum(int num) {
    this.rulePageNum.set(num);
  }

  public void setAiPageSize(int size) {
    this.aiPageSize.set(size);
  }

  public void setAiPageNum(int num) {
    this.aiPageNum.set(num);
  }

  public void setPqlPageSize(int size) {
    this.pqlPageSize.set(size);
  }

  public void setPqlPageNum(int num) {
    this.pqlPageNum.set(num);
  }
}
