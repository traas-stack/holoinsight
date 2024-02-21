/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRecordLatch;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRequest;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.service.event.AlertNotifyChainBuilder;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.biz.plugin.model.PluginContext;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.dal.converter.AlarmRuleConverter;
import io.holoinsight.server.home.dal.converter.AlertTemplateConverter;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.mapper.AlertTemplateMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.AlertTemplate;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.AlertTemplateDTO;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.NotificationTemplate;
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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
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
  @Resource
  private AlertTemplateMapper alertTemplateMapper;
  @Autowired
  private AlertTemplateConverter alertTemplateConverter;

  private static final String GATEWAY = "GatewayService";

  public boolean sendAlertNotifyV3(AlertNotifyRequest notify, AlertNotifyRecordLatch recordLatch) {
    String traceId = notify.getTraceId();
    LOGGER.info("{} receive_alarm_notify_request at {}, recover notify {}", traceId,
        this.environmentProperties.getDeploymentSite(), notify.isNotifyRecover());

    NotifyChain defaultNotifyChain = new NotifyChain(this.scheduleQueue);

    String tenant = notify.getTenant();
    String type = defaultNotifyChain.name;
    if (!notify.isNotifyRecover() && CollectionUtils.isEmpty(notify.getNotifyDataInfos())) {
      LOGGER.info("{} notify data info is empty.", traceId);
      RecordSucOrFailNotify.alertNotifyProcessFail(traceId + ": notify data info is empty ",
          GATEWAY, "check notify data info", notify.getAlertNotifyRecord());
      return true;
    }

    String ruleId = notify.getRuleId();
    if (!StringUtils.isNumeric(ruleId)) {
      LOGGER.warn("{} invalid rule {}", traceId, ruleId);
      RecordSucOrFailNotify.alertNotifyProcessFail(
          traceId + ": invalid rule fail; ruleId is " + ruleId, GATEWAY, "invalid rule",
          notify.getAlertNotifyRecord());
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
      RecordSucOrFailNotify.alertNotifyProcessFail(traceId + ": notifyChainList is empty, skip ",
          GATEWAY, " get notify chainList", notify.getAlertNotifyRecord());
      return true;
    }

    AlarmRule rawRule = this.alarmRuleMapper.selectById(Long.parseLong(ruleId));
    AlarmRuleDTO alertRule = this.alarmRuleConverter.doToDTO(rawRule);
    if (alertRule == null) {
      LOGGER.warn("{} can not find alarmRule by {}", traceId, ruleId);
      RecordSucOrFailNotify.alertNotifyProcessFail(
          traceId + ": can not find alarmRule by " + ruleId, GATEWAY, "find alarmRule",
          notify.getAlertNotifyRecord());
      return true;
    }

    AlertRuleExtra extra = alertRule.getExtra();
    notify.setAlertRuleExtra(extra);
    NotificationTemplate notificationTemplate = extra == null ? null : extra.getDingTalkTemplate();
    if (notificationTemplate == null) {
      String alertRuleAlertTemplateUuid = alertRule.getAlertTemplateUuid();
      if (StringUtils.isNotEmpty(alertRuleAlertTemplateUuid)) {
        AlertTemplate alertTemplate =
            this.alertTemplateMapper.selectById(alertRuleAlertTemplateUuid);
        if (alertTemplate != null) {
          AlertTemplateDTO templateDTO = this.alertTemplateConverter.doToDTO(alertTemplate);
          notificationTemplate = templateDTO.templateConfig;
        }
      }
    }
    notify.setNotificationTemplate(notificationTemplate);

    PluginContext pluginContext = buildNotifyContext(traceId, notify);
    RecordSucOrFailNotify.alertNotifyProcessSuc(GATEWAY, "send alert notify",
        notify.getAlertNotifyRecord());
    if (extra != null && extra.isRecord) {
      pluginContext.latch = new CountDownLatch(notifyChainList.size());
    }

    for (NotifyChain notifyChain : notifyChainList) {
      notifyChain.input(inputDatas, pluginContext);
      this.executorService.execute(notifyChain);
    }

    boolean status = true;
    try {
      if (pluginContext.latch != null) {
        status = pluginContext.latch.await(30, TimeUnit.SECONDS);
        if (!status) {
          throw new RuntimeException("the GatewayService waiting time elapsed");
        }
      }
    } catch (Exception e) {
      LOGGER.error("[ALERT_CountDownLatch][GatewayService] error {}", e.getMessage(), e);
    }
    if (status) {
      AlertNotifyRecordDTO alertNotifyRecord = pluginContext.getAlertNotifyRecord();
      LOGGER.info("plugin record data {} .", J.toJson(alertNotifyRecord));
      if (Objects.nonNull(recordLatch)) {
        recordLatch.add(alertNotifyRecord);
      }
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
    List<Map<String, Object>> res = new ArrayList<>();
    Map<Trigger, List<NotifyDataInfo>> notifyDataInfoMap = notifyRequest.getNotifyDataInfos();
    if (CollectionUtils.isEmpty(notifyDataInfoMap)) {
      Map<String, Object> item = new HashMap<>();
      item.put("alarmTime", notifyRequest.getAlarmTime());
      item.put("alarmLevel", notifyRequest.getAlarmLevel());
      item.put("alarmName", notifyRequest.getRuleName());
      item.put("tenant", notifyRequest.getTenant());
      res.add(item);
    } else {
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
    }

    return res;
  }
}
