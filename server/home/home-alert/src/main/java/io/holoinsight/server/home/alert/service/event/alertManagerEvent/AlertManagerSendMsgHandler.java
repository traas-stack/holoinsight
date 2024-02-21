/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.alertManagerEvent;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.common.http.XHttpResponse;
import io.holoinsight.server.home.alert.common.webhook.WebhookUtil;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/28 9:33 下午
 */
@Service
public class AlertManagerSendMsgHandler implements AlertHandlerExecutor {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertManagerSendMsgHandler.class);

  public void handle(List<AlertNotify> alarmNotifies) {

    // 发送单聊钉钉

    // 发送webhook
    // 查询所有租户的webhook
    try {
      alarmNotifies.forEach(alarmNotify -> {
        if (alarmNotify.getWebhookInfos() != null) {
          alarmNotify.getWebhookInfos().forEach(webhookInfo -> {
            XHttpResponse xHttpResponse = WebhookUtil.sendWebhook(webhookInfo);
            LOGGER.info("XHttpResponse: {} ", G.get().toJson(xHttpResponse));
          });
        }
      });

      LOGGER.info("AlarmSendWebhook SUCCESS {} ", G.get().toJson(alarmNotifies));
    } catch (Exception e) {
      LOGGER.error("AlarmSendWebhook Exception", e);
    }
    // 发送钉钉群机器人

    try {
      alarmNotifies.forEach(alarmNotify -> {
      });
      LOGGER.info("AlarmSendMsgHandler SUCCESS {} ", G.get().toJson(alarmNotifies));
    } catch (Exception e) {
      LOGGER.error("AlarmSendMsgHandler Exception", e);
    }

  }
}
