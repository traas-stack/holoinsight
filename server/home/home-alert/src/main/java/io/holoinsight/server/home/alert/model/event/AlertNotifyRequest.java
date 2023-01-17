/*
 * Copyright 2023 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.alert.model.data.InspectConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-01-12 21:40:00
 */
@Data
public class AlertNotifyRequest {

    private Map<String/* notify type */, List<String>> userNotifyMap; // 用户信息和通知渠道

    private List<String> dingdingUrls; // 钉钉群

    private List<WebhookInfo> webhookInfos; // 告警相关webhook消息

    private String traceId;

    private String tenant;

    private String uniqueId; // 告警规则+id

    private String ruleId; // 告警id

    private String ruleName; // 告警名称

    private Long alarmTime; // 告警时间

    private String alarmLevel; // 告警级别
//
//    private String metric; // 监控项
//
//    private String alarmTags; // 告警对象
//
//    private String alarmContent; // 告警内容
//
    private String aggregationNum; // 聚合告警数
//
//    private String ruleUrl; // 告警url
//
//    private String ruleConfig; // 告警配置规则
//
//    private String alarmTraceId; //告警唯一id

    private List<NotifyDataInfo> notifyDataInfos; //告警计算结果

    private InspectConfig ruleConfig; // 告警规则配置

}
