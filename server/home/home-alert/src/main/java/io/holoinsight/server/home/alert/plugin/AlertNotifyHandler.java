/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRecordLatch;
import io.holoinsight.server.home.alert.model.event.AlertNotifyRequest;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author masaimu
 * @version 2023-01-12 21:35:00
 */
public abstract class AlertNotifyHandler implements AlertHandlerExecutor {
  private static Logger LOGGER = LoggerFactory.getLogger(AlertNotifyHandler.class);

  @Resource
  private GatewayService gatewayService;

  private static final String NOTIFY_HANDLER = "AlertNotifyHandler";

  @Override
  public void handle(List<AlertNotify> alertNotifies) {
    if (CollectionUtils.isEmpty(alertNotifies)) {
      LOGGER.info("alertNotifies is empty.");
    }
    int count = 0;
    int latchSize = 0;
    for (AlertNotify alertNotify : alertNotifies) {
      if (alertNotify.isAlertRecord()) {
        latchSize++;
      }
    }
    AlertNotifyRecordLatch recordLatch = new AlertNotifyRecordLatch();
    List<AlertNotifyRecordDTO> alertNotifyRecordDTOList = new ArrayList<>();
    CountDownLatch latch = null;
    if (latchSize > 0) {
      latch = new CountDownLatch(latchSize);
    }
    recordLatch.setAlertNotifyRecordDTOList(alertNotifyRecordDTOList);

    for (AlertNotify alertNotify : alertNotifies) {
      try {
        LOGGER.info("{} alert notify handle begin.", alertNotify.getTraceId());
        if (alertNotify.nonNotifyRecover()) {
          LOGGER.info("{} alert notify is recover.", alertNotify.getTraceId());
          continue;
        }
        count++;
        sendPlugin(alertNotify, recordLatch);
      } catch (Throwable e) {
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertNotifyHandler][1] {} fail to alert_notify_handle for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
        RecordSucOrFailNotify.alertNotifyProcessFail(
            "fail to alert notify handle for " + e.getMessage(), NOTIFY_HANDLER,
            "alert notify handle", alertNotify.getAlertNotifyRecord());
      } finally {
        if (latch != null && alertNotify.isAlertRecord()) {
          latch.countDown();
        }
      }
    }
    boolean status = true;
    try {
      if (latch != null) {
        status = latch.await(30, TimeUnit.SECONDS);
        if (!status) {
          throw new RuntimeException("the AlertTaskCompute waiting time elapsed");
        }
      }
    } catch (Exception e) {
      LOGGER.error("[ALERT_CountDownLatch][AlertTaskCompute] error {}", e.getMessage(), e);
    }
    if (status) {
      LOGGER.info("alert notify record data size {} .", recordLatch.size());
      // RecordSucOrFailNotify.batchInsert(recordLatch.getAlertNotifyRecordDTOList());
    }

    LOGGER.info("alert_notification_notify_step size [{}]", count);
  }

  private void sendPlugin(AlertNotify alertNotify, AlertNotifyRecordLatch recordLatch) {
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
    alertNotifyRequest.setAlertIp(alertNotify.getAlertIp());
    alertNotifyRequest.setPid(getPid(alertNotify));
    alertNotifyRequest.setTenant(getTenant(alertNotify));
    alertNotifyRequest.setWorkspace(getWorkspace(alertNotify));
    alertNotifyRequest.setLogAnalysis(alertNotify.getLogAnalysis());
    alertNotifyRequest.setLogSample(alertNotify.getLogSample());
    alertNotifyRequest.setNotifyRecover(alertNotify.notifyRecover());

    alertNotifyRequest.setAlertNotifyRecord(alertNotify.getAlertNotifyRecord());

    if (!CollectionUtils.isEmpty(alertNotify.getNotifyDataInfos())) {
      alertNotifyRequest.setNotifyDataInfos(alertNotify.getNotifyDataInfos());
    }
    LOGGER.info("{} begin to send alert notify to gateway", alertNotify.getTraceId());
    RecordSucOrFailNotify.alertNotifyProcessSuc(NOTIFY_HANDLER, "alert notify handle",
        alertNotify.getAlertNotifyRecord());
    boolean success = this.gatewayService.sendAlertNotifyV3(alertNotifyRequest, recordLatch);

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

  protected abstract String getPid(AlertNotify alertNotify);

  protected abstract String getAppKey();
}
