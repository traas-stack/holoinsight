/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRequest;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.service.event.AlertNotifyChainBuilder;
import io.holoinsight.server.home.biz.plugin.model.PluginContext;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.dal.converter.AlarmRuleConverter;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangsiyuan
 * @date 2022/8/8 2:19 下午
 */
public abstract class GatewayService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);
  ThreadPoolExecutor executorService =
      new ThreadPoolExecutor(10, 50, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

  @Autowired
  private IntegrationPluginService integrationPluginService;
  @Resource
  private AlarmRuleMapper alarmRuleMapper;
  @Autowired
  private PluginScheduleQueue scheduleQueue;
  @Autowired
  private AlertNotifyChainBuilder alertNotifyChainBuilder;
  @Autowired
  private EnvironmentProperties environmentProperties;
  @Resource
  private AlarmRuleConverter alarmRuleConverter;

  public boolean sendAlertNotifyV3(AlertNotifyRequest notify) {
    String traceId = notify.getTraceId();
    LOGGER.info("{} receive_alarm_notify_request at {}", traceId,
        this.environmentProperties.getDeploymentSite());

    NotifyChain defaultNotifyChain = new NotifyChain(this.scheduleQueue);

    String tenant = notify.getTenant();
    String type = defaultNotifyChain.name;
    if (CollectionUtils.isEmpty(notify.getNotifyDataInfos())) {
      LOGGER.info("{} notify data info is empty.", traceId);
      return true;
    }

    String ruleId = notify.getRuleId();
    if (!StringUtils.isNumeric(ruleId)) {
      LOGGER.warn("{} invalid rule {}", traceId, ruleId);
      return true;
    }

    String uniqueId = notify.getUniqueId();

    List<Map<String, Object>> inputDatas = convertToInput(notify);
    String name = String.join("_", tenant, type, uniqueId);
    List<IntegrationPluginDTO> integrationPlugins =
        this.integrationPluginService.findByMap(Collections.singletonMap("name", name));

    List<NotifyChain> notifyChainList =
        this.alertNotifyChainBuilder.buildNotifyChains(traceId, integrationPlugins);

    if (CollectionUtils.isEmpty(notifyChainList)) {
      LOGGER.info("{} {} notifyChainList is empty, skip.", traceId, ruleId);
      return true;
    }

    AlarmRule rawRule = this.alarmRuleMapper.selectById(Long.parseLong(ruleId));
    AlarmRuleDTO alertRule = this.alarmRuleConverter.doToDTO(rawRule);
    if (alertRule == null) {
      LOGGER.warn("{} can not find alarmRule by {}", traceId, ruleId);
      return true;
    }

    AlertRuleExtra extra = alertRule.getExtra();
    notify.setAlertRuleExtra(extra);

    PluginContext pluginContext = buildNotifyContext(traceId, notify);
    for (NotifyChain notifyChain : notifyChainList) {
      notifyChain.input(inputDatas, pluginContext);
      this.executorService.execute(notifyChain);
    }

    return true;
  }

  protected abstract PluginContext buildNotifyContext(String traceId, AlertNotifyRequest notify);

  /**
   * alarmTime,alarmLevel,alarmName,tenant,triggerContent, currentValue, tags
   *
   * @param notifyRequest
   * @return
   */
  private List<Map<String, Object>> convertToInput(AlertNotifyRequest notifyRequest) {
    Map<Trigger, List<NotifyDataInfo>> notifyDataInfoMap = notifyRequest.getNotifyDataInfos();
    if (CollectionUtils.isEmpty(notifyDataInfoMap)) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> res = new ArrayList<>();
    for (Map.Entry<Trigger, List<NotifyDataInfo>> entry : notifyDataInfoMap.entrySet()) {
      for (NotifyDataInfo notifyDataInfo : entry.getValue()) {
        Map<String, Object> item = new HashMap<>();
        item.put("alarmTime", notifyRequest.getAlarmTime());
        item.put("alarmLevel", notifyRequest.getAlarmLevel());
        item.put("alarmName", notifyRequest.getRuleName());
        item.put("tenant", notifyRequest.getTenant());
        item.put("triggerContent", notifyDataInfo.getTriggerContent());
        item.put("currentValue", notifyDataInfo.getCurrentValue());
        item.put("tags", notifyDataInfo.getTags());
        res.add(item);
      }
    }

    return res;
  }
}
