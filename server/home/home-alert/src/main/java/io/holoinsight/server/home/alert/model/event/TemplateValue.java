/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import com.alibaba.fastjson.JSON;
import io.holoinsight.server.home.alert.common.AlarmContentGenerator;
import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.emuns.AlarmLevel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/3/29 8:10 下午
 */
@Data
public class TemplateValue {

    private static Logger LOGGER = LoggerFactory.getLogger(TemplateValue.class);

    String tenant; // 租户

    String ruleId; // 规则id

    String uniqueId; // 唯一id

    String ruleName; // 规则名称

    String alarmTime; // 告警时间

    String alarmTimestamp; // 告警时间戳

    String metric; // 监控项

    String alarmLevel; // 告警级别

    String alarmTags; // 告警对象

    String alarmContent; // 告警内容

    String aggregationNum; // 聚合告警数

    String ruleUrl; // 告警url

    String ruleConfig; // 告警配置规则

    String alarmTraceId; //告警唯一id

    /**
     * 获取第一个通知信息，转换。
     * 减少信息
     *
     * @param alertNotify
     * @param domain
     * @return
     */
    public static List<TemplateValue> convertFirstAlertNotify(AlertNotify alertNotify, String domain) {
        List<TemplateValue> templateValues = new ArrayList<>();
        Date day = new Date(alertNotify.getAlarmTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOGGER.info("convertAlarmNotify timeZone {} ", JSON.toJSONString(sdf.getTimeZone()));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        TemplateValue templateValue = new TemplateValue();
        templateValue.setTenant(alertNotify.getTenant());
        templateValue.setUniqueId(alertNotify.getUniqueId());
        templateValue.setRuleId(alertNotify.getUniqueId().split("_")[1]);
        templateValue.setRuleName(alertNotify.getRuleName());
        templateValue.setAlarmTime(sdf.format(day));
        templateValue.setAlarmTimestamp(String.valueOf(alertNotify.getAlarmTime()));
        templateValue.setAlarmLevel(AlarmLevel.getDesc(alertNotify.getAlarmLevel()));

        if (alertNotify.isPqlNotify()) {
            templateValue.setMetric(alertNotify.getPqlRule().getPql());
            List<Map<String, String>> tagList = alertNotify.getPqlRule().getDataResult().stream().map(DataResult::getTags).collect(Collectors.toList());
            templateValue.setAlarmTags(JSON.toJSONString(tagList));
            templateValue.setAlarmContent(AlarmContentGenerator.genPqlAlarmContent(alertNotify.getPqlRule().getPql(), alertNotify.getPqlRule().getDataResult()));
        } else {
            List<NotifyDataInfo> notifyDataInfos = new ArrayList<>();
            alertNotify.getNotifyDataInfos().forEach((key, value) -> {
                notifyDataInfos.addAll(value);
            });
            // 随机获取一个信息，其余信息通过接口查询
            NotifyDataInfo notifyDataInfo = notifyDataInfos.get(0);
            templateValue.setMetric(notifyDataInfo.getMetric());
            templateValue.setAlarmTags(JSON.toJSONString(notifyDataInfo.getTags()));
            templateValue.setAlarmContent(notifyDataInfo.getTriggerContent() + "当前值为：" + notifyDataInfo.getCurrentValue());
        }
        templateValue.setAggregationNum(String.valueOf(alertNotify.getAggregationNum()));
        templateValue.setRuleUrl(domain + templateValue.getRuleId() + "?tenant=" + templateValue.getTenant());
        templateValue.setRuleConfig(JSON.toJSONString(alertNotify.getRuleConfig()));
        templateValue.setAlarmTraceId(alertNotify.getAlarmTraceId());
        templateValues.add(templateValue);
        return templateValues;
    }

    /**
     * 转换所有的
     *
     * @param alertNotify
     * @param domain
     * @return
     */
    public static List<TemplateValue> convertAllAlertNotify(AlertNotify alertNotify, String domain) {
        List<TemplateValue> templateValues = new ArrayList<>();
        Date day = new Date(alertNotify.getAlarmTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LOGGER.info("convertAlarmNotify timeZone {} ", JSON.toJSONString(sdf.getTimeZone()));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        List<NotifyDataInfo> notifyDataInfos = new ArrayList<>();
        alertNotify.getNotifyDataInfos().forEach((key, value) -> {
            notifyDataInfos.addAll(value);
        });
        if (CollectionUtils.isEmpty(notifyDataInfos)) {
            return templateValues;
        }
        for (NotifyDataInfo notifyDataInfo : notifyDataInfos) {
            TemplateValue templateValue = new TemplateValue();
            templateValue.setTenant(alertNotify.getTenant());
            templateValue.setUniqueId(alertNotify.getUniqueId());
            templateValue.setRuleId(alertNotify.getUniqueId().split("_")[1]);
            templateValue.setRuleName(alertNotify.getRuleName());
            templateValue.setAlarmTime(sdf.format(day));
            templateValue.setAlarmTimestamp(String.valueOf(alertNotify.getAlarmTime()));
            templateValue.setAlarmLevel(AlarmLevel.getDesc(alertNotify.getAlarmLevel()));
            templateValue.setMetric(notifyDataInfo.getMetric());
            templateValue.setAlarmTags(JSON.toJSONString(notifyDataInfo.getTags()));
            templateValue.setAlarmContent(notifyDataInfo.getTriggerContent() + "当前值为：" + notifyDataInfo.getCurrentValue());
            templateValue.setAggregationNum(String.valueOf(alertNotify.getAggregationNum()));
            templateValue.setRuleUrl(domain + templateValue.getRuleId() + "?tenant=" + templateValue.getTenant());
            templateValue.setRuleConfig(JSON.toJSONString(alertNotify.getRuleConfig()));
            templateValue.setAlarmTraceId(alertNotify.getAlarmTraceId());
            templateValues.add(templateValue);
        }
        return templateValues;
    }

    public static List<TemplateValue> convertAlertNotify(AlertNotify alertNotify) {
        List<TemplateValue> templateValues = new ArrayList<>();
        Date day = new Date(alertNotify.getAlarmTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TemplateValue templateValue = new TemplateValue();
        templateValue.setRuleName(alertNotify.getRuleName());
        templateValue.setAlarmTime(sdf.format(day));
        templateValue.setAlarmLevel(AlarmLevel.getDesc(alertNotify.getAlarmLevel()));
        templateValues.add(templateValue);
        return templateValues;
    }

    public static TemplateValue convertWebhook(WebhookInfo webhookInfo){
        TemplateValue templateValues = new TemplateValue();

        return templateValues;
    }



}
