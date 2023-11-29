/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.plugin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.model.event.WebhookInfo;
import io.holoinsight.server.home.alert.service.converter.DoConvert;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.dal.mapper.AlarmBlockMapper;
import io.holoinsight.server.home.dal.mapper.AlarmDingDingRobotMapper;
import io.holoinsight.server.home.dal.mapper.AlarmGroupMapper;
import io.holoinsight.server.home.dal.mapper.AlarmSubscribeMapper;
import io.holoinsight.server.home.dal.mapper.AlarmWebhookMapper;
import io.holoinsight.server.home.dal.model.AlarmBlock;
import io.holoinsight.server.home.dal.model.AlarmDingDingRobot;
import io.holoinsight.server.home.dal.model.AlarmGroup;
import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.AlarmWebhook;
import io.holoinsight.server.home.facade.AlertSilenceConfig;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.PqlRule;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author masaimu
 * @version 2023-01-12 20:34:00
 */
@Service
public class GetSubscriptionHandler implements AlertHandlerExecutor {

  private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriptionHandler.class);

  @Resource
  private AlarmGroupMapper alarmGroupDOMapper;

  @Resource
  private AlarmDingDingRobotMapper alarmDingDingRobotDOMapper;

  @Resource
  private AlarmSubscribeMapper alarmSubscribeDOMapper;

  @Resource
  private AlarmWebhookMapper alarmWebhookDOMapper;

  @Resource
  private AlarmBlockMapper alarmBlockDOMapper;

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  private static final String GET_SUBSCRIPTION = "GetSubscriptionHandler";

  @Override
  public void handle(List<AlertNotify> alertNotifies) {
    try {

      Map<String, List<AlarmWebhook>> alertWebhookMap = getWebhookMap(alertNotifies);
      LOGGER.info("AlertWebhookMap SUCCESS {} ", G.get().toJson(alertWebhookMap));

      handleBlock(alertNotifies);

      // 查询消息通知订阅关系
      alertNotifies.parallelStream().forEach(alertNotify -> {
        try {
          QueryWrapper<AlarmSubscribe> alertSubscribeQueryWrapper = new QueryWrapper<>();
          alertSubscribeQueryWrapper.eq("unique_id", alertNotify.getUniqueId());
          alertSubscribeQueryWrapper.eq("status", (byte) 1);
          requestContextAdapter.queryWrapperTenantAdapt(alertSubscribeQueryWrapper, null,
              alertNotify.getWorkspace());
          addGlobalWebhook(alertNotify, alertWebhookMap);
          List<AlarmSubscribe> alertSubscribeList =
              alarmSubscribeDOMapper.selectList(alertSubscribeQueryWrapper);
          LOGGER.info("{} GetSubscription_SUCCESS {} {} ", alertNotify.getTraceId(),
              alertNotify.getUniqueId(), G.get().toJson(alertSubscribeList));

          InspectConfig inspectConfig = alertNotify.getRuleConfig();
          if (keepSilence(inspectConfig.getAlertSilenceConfig(), alertNotify.getAlarmTime())) {
            LOGGER.info("{} keep silence {}.", alertNotify.getTraceId(),
                J.toJson(inspectConfig.getAlertSilenceConfig()));
            return;
          }

          if (!CollectionUtils.isEmpty(alertSubscribeList)) {
            Set<String> userIdList = new HashSet<>();
            Set<Long> dingDingGroupIdList = new HashSet<>();
            List<WebhookInfo> webhookInfos = new ArrayList<>();
            Map<String/* notify type */, List<String>> userNotifyMap = new HashMap<>();
            for (AlarmSubscribe alertSubscribe : alertSubscribeList) {
              String noticeTypeStr = alertSubscribe.getNoticeType();
              if (StringUtils.isEmpty(noticeTypeStr) || !noticeTypeStr.startsWith("[")) {
                LOGGER.warn("{} invalid noticeTypeStr {}", alertNotify.getTraceId(), noticeTypeStr);
                continue;
              }
              List<String> noticeTypeList = G.parseList(noticeTypeStr, String.class);
              for (String noticeType : noticeTypeList) {
                switch (noticeType) {
                  case "dingDing":
                  case "sms":
                  case "email":
                  case "phone":
                    List<String> personList = null;
                    if (isNotifyGroup(alertSubscribe.getGroupId())) {
                      QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();
                      wrapper.eq("id", alertSubscribe.getGroupId());
                      AlarmGroup alarmGroup = alarmGroupDOMapper.selectOne(wrapper);
                      if (alarmGroup == null) {
                        break;
                      }
                      Map<String, List<String>> map =
                          G.get().fromJson(alarmGroup.getGroupInfo(), Map.class);
                      personList = map.get("person");
                    } else if (alertSubscribe.getSubscriber() != null) {
                      String userId = alertSubscribe.getSubscriber();
                      personList = new ArrayList<>();
                      personList.add(userId);
                    }
                    if (!CollectionUtils.isEmpty(personList)) {
                      List<String> list =
                          userNotifyMap.computeIfAbsent(noticeType, k -> new ArrayList<>());
                      list.addAll(personList);
                    }
                    break;
                  case "dingDingRobot":
                    String subscriber = alertSubscribe.getSubscriber();
                    if (StringUtils.isNotBlank(subscriber) && StringUtils.isNumeric(subscriber)) {
                      dingDingGroupIdList.add(Long.parseLong(subscriber));
                    }
                    if (isNotifyGroup(alertSubscribe.getGroupId())) {
                      QueryWrapper<AlarmGroup> wrapper = new QueryWrapper<>();
                      wrapper.eq("id", alertSubscribe.getGroupId());
                      AlarmGroup alarmGroup = alarmGroupDOMapper.selectOne(wrapper);
                      if (StringUtils.isNotBlank(alarmGroup.getGroupInfo())) {
                        Map<String, List<String>> map =
                            G.get().fromJson(alarmGroup.getGroupInfo(), Map.class);
                        List<String> ddRobotIds = map.get("ding_ding_robot");
                        if (!CollectionUtils.isEmpty(ddRobotIds)) {
                          for (String id : ddRobotIds) {
                            dingDingGroupIdList.add(Long.parseLong(id));
                          }
                        }
                      }
                    }
                    break;
                  case "webhook":
                    if (!alertNotify.getIsRecover()) {
                      AlarmWebhook alertWebhook =
                          alarmWebhookDOMapper.selectById(alertSubscribe.getGroupId());
                      if (alertWebhook != null && alertWebhook.getStatus().equals((byte) 1)) {
                        webhookInfos.add(DoConvert.alertWebhookDoConverter(alertWebhook));
                      }
                      LOGGER.info("{} webhookInfos is {}.", alertNotify.getTraceId(),
                          G.get().toJson(webhookInfos));
                    } else {
                      LOGGER.info("{} alert is recover.", alertNotify.getTraceId());
                    }
                    break;
                }
              }
            }
            alertNotify.setUserNotifyMap(userNotifyMap);
            if (!CollectionUtils.isEmpty(webhookInfos)) {
              alertNotify.getWebhookInfos().addAll(webhookInfos);
            }

            if (!CollectionUtils.isEmpty(dingDingGroupIdList)) {
              QueryWrapper<AlarmDingDingRobot> wrapper = new QueryWrapper<>();
              wrapper.in("id", new ArrayList<>(dingDingGroupIdList));
              List<AlarmDingDingRobot> alertDingDingRobotList =
                  alarmDingDingRobotDOMapper.selectList(wrapper);
              List<WebhookInfo> dingdingUrls = new ArrayList<>();
              for (AlarmDingDingRobot alarmDingDingRobot : alertDingDingRobotList) {
                WebhookInfo webhookInfo = new WebhookInfo();
                webhookInfo.setRequestUrl(alarmDingDingRobot.getRobotUrl());
                webhookInfo.setExtra(alarmDingDingRobot.getExtra());
                dingdingUrls.add(webhookInfo);
              }
              alertNotify.setDingdingUrl(dingdingUrls);
            }
          }
          RecordSucOrFailNotify.alertNotifyProcessSuc(GET_SUBSCRIPTION, "query subscription",
              alertNotify.getAlertNotifyRecord());
        } catch (Throwable e) {
          LOGGER.error(
              "[HoloinsightAlertInternalException][GetSubscriptionHandler][1] {}  fail to get_subscription for {}",
              alertNotify.getTraceId(), e.getMessage(), e);
          RecordSucOrFailNotify.alertNotifyProcess("fail to get_subscription for " + e.getMessage(),
              GET_SUBSCRIPTION, "query subscription", alertNotify.getAlertNotifyRecord());
        }
      });
      LOGGER.info("[GetSubscriptionHandler][{}] finish to get_subscription.", alertNotifies.size());
    } catch (Exception e) {
      LOGGER.error(
          "[HoloinsightAlertInternalException][GetSubscriptionHandler][{}] fail to get_subscription for {}",
          alertNotifies.size(), e.getMessage(), e);
    }
  }

  private boolean keepSilence(AlertSilenceConfig alertSilenceConfig, Long alarmTime) {
    return alertSilenceConfig != null
        && !StringUtils.equals(alertSilenceConfig.getSilenceMode(), "default")
        && !alertSilenceConfig.needShoot(alarmTime);
  }

  private void handleBlock(List<AlertNotify> alertNotifies) {
    Map<String, AlarmBlock> alertBlockMap = getBlockMap(alertNotifies);

    LOGGER.info("alert block size {}", alertBlockMap.size());

    Iterator<AlertNotify> iterator = alertNotifies.iterator();
    while (iterator.hasNext()) {
      AlertNotify alertNotify = iterator.next();

      if (alertNotify.getIsRecover()) {
        RecordSucOrFailNotify.alertNotifyProcess("alertNotify is recover", GET_SUBSCRIPTION,
            "remove block subscription", alertNotify.getAlertNotifyRecord());
        iterator.remove();
        LOGGER.info("{} alert rule {} has recovered.", alertNotify.getTraceId(),
            alertNotify.getUniqueId());
        continue;
      }

      AlarmBlock alertBlock = alertBlockMap.get(alertNotify.getUniqueId());
      if (alertBlock == null) {
        continue;
      }

      if (StringUtils.isEmpty(alertBlock.getTags()) || alertBlock.getTags().equals("{}")) {
        RecordSucOrFailNotify.alertNotifyProcess("alertNotify is blocked", GET_SUBSCRIPTION,
            "remove block subscription", alertNotify.getAlertNotifyRecord());
        iterator.remove();
        LOGGER.info("{} alert rule {} has been blocked.", alertNotify.getTraceId(),
            alertNotify.getUniqueId());
        continue;
      }

      Map<String, String> tagMap =
          G.get().fromJson(alertBlock.getTags(), new TypeToken<Map<String, String>>() {}.getType());

      if (!CollectionUtils.isEmpty(tagMap)) {
        if (alertNotify.isPqlNotify()) {
          PqlRule pqlRule = alertNotify.getPqlRule();
          Iterator<DataResult> it = pqlRule.getDataResult().iterator();
          while (it.hasNext()) {
            DataResult dataResult = it.next();
            Map<String, String> notifyTags = dataResult.getTags();
            notifyTags.forEach((key, value) -> {
              if (tagMap.containsKey(key)) {
                Pattern pattern = Pattern.compile(tagMap.get(key));
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                  LOGGER.info("{} pql alert rule {} tag {} {} has been blocked.",
                      alertNotify.getTraceId(), alertNotify.getUniqueId(), key, value);
                  it.remove();
                }
              }
            });
          }
          if (CollectionUtils.isEmpty(pqlRule.getDataResult())) {
            LOGGER.info("{} pql alert rule {} has been blocked because all tags have been blocked.",
                alertNotify.getTraceId(), alertNotify.getUniqueId());
            iterator.remove();
          }
        } else {
          Map<Trigger, List<NotifyDataInfo>> notifyDataInfos = new HashMap<>();
          alertNotify.getNotifyDataInfos().forEach((trigger, notifyDataInfoList) -> {
            Iterator<NotifyDataInfo> it = notifyDataInfoList.iterator();
            // Remove NotifyDataInfo that need be blocked
            while (it.hasNext()) {
              NotifyDataInfo notifyDataInfo = it.next();
              Map<String, String> notifyTags = notifyDataInfo.getTags();
              notifyTags.forEach((key, value) -> {
                if (tagMap.containsKey(key)) {
                  Pattern pattern = Pattern.compile(tagMap.get(key));
                  Matcher matcher = pattern.matcher(value);
                  if (matcher.find()) {
                    LOGGER.info("{} alert rule {} tag {} {} has been blocked.",
                        alertNotify.getTraceId(), alertNotify.getUniqueId(), key, value);
                    it.remove();
                  }
                }
              });
            }
            if (!CollectionUtils.isEmpty(notifyDataInfoList)) {
              notifyDataInfos.put(trigger, notifyDataInfoList);
            }
          });
          if (CollectionUtils.isEmpty(notifyDataInfos)) {
            LOGGER.info("{} alert rule {} has been blocked because all tags have been blocked.",
                alertNotify.getTraceId(), alertNotify.getUniqueId());
            iterator.remove();
          } else {
            alertNotify.setNotifyDataInfos(notifyDataInfos);
          }
        }
      }

      RecordSucOrFailNotify.alertNotifyProcessSuc(GET_SUBSCRIPTION, "remove block subscription",
          alertNotify.getAlertNotifyRecord());
    }
  }

  private void addGlobalWebhook(AlertNotify alertNotify,
      Map<String, List<AlarmWebhook>> alertWebhookMap) {
    List<WebhookInfo> webhookInfos = new ArrayList<>();
    List<AlarmWebhook> alertWebhookList = alertWebhookMap.get(alertNotify.getTenant());
    if (!CollectionUtils.isEmpty(alertWebhookList)) {
      webhookInfos.addAll(alertWebhookList.stream().map(DoConvert::alertWebhookDoConverter)
          .collect(Collectors.toList()));
    }
    LOGGER.info("{} global webhookInfos is {}.", alertNotify.getTraceId(),
        G.get().toJson(webhookInfos));
    alertNotify.getWebhookInfos().addAll(webhookInfos);
  }

  private boolean isNotifyGroup(Long groupId) {
    return groupId != null && groupId != -1;
  }

  /**
   * Get block alerts
   *
   * @param alertNotifies
   * @return
   */
  protected Map<String, AlarmBlock> getBlockMap(List<AlertNotify> alertNotifies) {
    List<String> uniqueIds =
        alertNotifies.stream().map(AlertNotify::getUniqueId).collect(Collectors.toList());
    QueryWrapper<AlarmBlock> wrapper = new QueryWrapper<>();
    wrapper.in("unique_id", uniqueIds);
    wrapper.ge("end_time", new Date());
    List<AlarmBlock> alertBlockList = alarmBlockDOMapper.selectList(wrapper);
    Map<String, AlarmBlock> alertBlockMap = new HashMap<>();
    if (CollectionUtils.isEmpty(alertBlockList)) {
      return alertBlockMap;
    }
    for (AlarmBlock block : alertBlockList) {
      alertBlockMap.put(block.getUniqueId(), block);
    }
    return alertBlockMap;
  }

  /**
   * Get webhook msg
   *
   * @param alertNotifies
   * @return
   */
  private Map<String, List<AlarmWebhook>> getWebhookMap(List<AlertNotify> alertNotifies) {
    List<String> tenantList =
        alertNotifies.stream().filter(alertNotify -> !alertNotify.getIsRecover())
            .map(AlertNotify::getTenant).collect(Collectors.toList());
    Map<String, List<AlarmWebhook>> alertWebhookMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(tenantList)) {
      QueryWrapper<AlarmWebhook> wrapper = new QueryWrapper<>();
      wrapper.in("tenant", tenantList);
      wrapper.eq("type", (byte) 1);
      wrapper.eq("status", (byte) 1);
      List<AlarmWebhook> alertWebhookList = alarmWebhookDOMapper.selectList(wrapper);
      alertWebhookMap =
          alertWebhookList.stream().collect(Collectors.groupingBy(AlarmWebhook::getTenant));
    }
    return alertWebhookMap;
  }
}
