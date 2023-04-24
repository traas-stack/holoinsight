/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.service.FuseProtector;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRequest;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static io.holoinsight.server.common.service.FuseProtector.NORMAL_AlertNotifyHandler;

/**
 * @author masaimu
 * @version 2023-01-12 21:35:00
 */
public abstract class AlertNotifyHandler implements AlertHandlerExecutor {
  private static Logger LOGGER = LoggerFactory.getLogger(AlertNotifyHandler.class);

  @Resource
  private GatewayService gatewayService;

  @Override
  public void handle(List<AlertNotify> alertNotifies) {
    if (CollectionUtils.isEmpty(alertNotifies)) {
      LOGGER.info("alertNotifies is empty.");
    }
    int count = 0;
    for (AlertNotify alertNotify : alertNotifies) {
      try {
        LOGGER.info("{} alert notify handle begin.", alertNotify.getTraceId());
        if (alertNotify.getIsRecover() != null && alertNotify.getIsRecover()) {
          LOGGER.info("{} alert notify is recover.", alertNotify.getTraceId());
          continue;
        }
        count++;
        sendPlugin(alertNotify);
        FuseProtector.voteComplete(NORMAL_AlertNotifyHandler);
      } catch (Throwable e) {
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertNotifyHandler][1] {} fail to alert_notify_handle for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
        FuseProtector.voteNormalError(NORMAL_AlertNotifyHandler, e.getMessage());
      }
    }
    LOGGER.info("alert_notification_notify_step size [{}]", count);
  }

  private void sendPlugin(AlertNotify alertNotify) {
    AlertNotifyRequest alertNotifyRequest = new AlertNotifyRequest();
    alertNotifyRequest.setTraceId(alertNotify.getTraceId());
    alertNotifyRequest.setUniqueId(alertNotify.getUniqueId());
    alertNotifyRequest.setRuleId(alertNotify.getUniqueId().split("_")[1]);
    alertNotifyRequest.setAlarmTime(alertNotify.getAlarmTime());
    alertNotifyRequest.setDingdingUrls(alertNotify.getDingdingUrl());

    alertNotifyRequest.setWebhookInfos(alertNotify.getWebhookInfos());
    alertNotifyRequest.setUserNotifyMap(alertNotify.getUserNotifyMap());
    alertNotifyRequest.setAlarmLevel(alertNotify.getAlarmLevel());
    alertNotifyRequest.setAggregationNum(String.valueOf(alertNotify.getAggregationNum()));
    alertNotifyRequest.setRuleConfig(alertNotify.getRuleConfig());
    alertNotifyRequest.setRuleName(alertNotify.getRuleName());
    alertNotifyRequest.setEnvType(alertNotify.getEnvType());
    alertNotifyRequest.setAlarmHistoryId(alertNotify.getAlarmHistoryId());
    alertNotifyRequest.setAlarmHistoryDetailId(alertNotify.getAlarmHistoryDetailId());
    alertNotifyRequest.setSourceType(alertNotify.getSourceType());
    alertNotifyRequest.setDuration(alertNotify.getDuration());
    alertNotifyRequest.setAlertServer(alertNotify.getAlertServer());

    alertNotifyRequest.setTenant(getTenant(alertNotify));
    alertNotifyRequest.setWorkspace(getWorkspace(alertNotify));
    alertNotifyRequest.setLogAnalysis(alertNotify.getLogAnalysis());

    if (!CollectionUtils.isEmpty(alertNotify.getNotifyDataInfos())) {
      alertNotifyRequest.setNotifyDataInfos(alertNotify.getNotifyDataInfos());
    }
    LOGGER.info("{} begin to send alert notify to gateway for {}", alertNotify.getTraceId(),
        G.get().toJson(alertNotifyRequest));
    boolean success = this.gatewayService.sendAlertNotifyV3(alertNotifyRequest);

    if (!success) {
      LOGGER.error("{} AlarmNotifyHandler send notify Exception Request:{}",
          alertNotify.getTraceId(), J.toJson(alertNotifyRequest));
    } else {
      LOGGER.info("{} AlarmNotifyHandler send notify SUCCESS {}", alertNotify.getTraceId(),
          alertNotify.getUniqueId());
    }
  }

  protected abstract String getWorkspace(AlertNotify alertNotify);

  protected abstract String getTenant(AlertNotify alertNotify);
}
