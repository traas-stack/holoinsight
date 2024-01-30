/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.alertManagerEvent;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.common.AlarmConstant;
import io.holoinsight.server.home.alert.common.AlarmRegexUtil;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.common.ObjectToMapUtil;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.ElementSpiEnum;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.alert.service.event.element.ElementSpiServiceFactory;
import io.holoinsight.server.home.common.exception.HoloinsightAlertInternalException;
import io.holoinsight.server.home.facade.TemplateValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangsiyuan
 * @date 2022/3/28 9:29 下午
 */
@Service
public class AlertManagerBuildMsgHandler implements AlertHandlerExecutor {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertManagerBuildMsgHandler.class);

  // 模板正则提取
  public static final String ALARM_TEMPLATE_PATTERN = "(?<=\\$\\{).*?(?=\\})";

  public static String generateTemplate() {
    String message = "**告警名称**：${ruleName}  \n" + "**告警时间**：${alarmTime}  \n" +
    // "**监控项**：${metric}告警 \n" +
        "**告警级别**：${alarmLevel}  \n" + "**告警对象**：${alarmTags}  \n" + "**告警简述**：${alarmContent} \n";
    // "<if test=\" aggregationNum != null and aggregationNum != 0\" >" +
    // "**合并个数**：${aggregationNum} \n" +
    // "</if>";
    return message;
  }

  public void handle(List<AlertNotify> alarmNotifies) {

    try {
      alarmNotifies.parallelStream().forEach(alarmNotify -> {
        // 后续增加自定义模板
        List<TemplateValue> templateValues = AlertNotify.convertAlertNotify(alarmNotify);
        String markDownTemplate = generateTemplate();
        templateValues.forEach(e -> {
          String markDownMsg = buildMsgWithTemplate(markDownTemplate, e);
          if (alarmNotify.getMsgList() == null) {
            alarmNotify.setMsgList(new ArrayList<>());
          }
          alarmNotify.getMsgList().add(markDownMsg);
        });
      });
      LOGGER.info("AlertManagerBuildMsgHandler SUCCESS {} ", G.get().toJson(alarmNotifies));
    } catch (Exception e) {
      LOGGER.error("AlertManagerBuildMsgHandler Exception", e);
    }

  }

  public static String buildMsgWithTemplate(String template, TemplateValue values) {
    if (values == null) {
      return "";
    }
    String content = "";
    try {
      // 实体转换为map
      Map<String, String> map = ObjectToMapUtil.generateObjectToStringMap(values);
      map.put("alarmLevel", values.getAlarmLevel() == null ? "" : values.getAlarmLevel().getDesc());
      content = buildMsgWithMap(template, map, Collections.emptyList());
    } catch (Exception e) {
      // This catch statement is intentionally empty
    }
    // 模板参数全局替换
    return content;
  }

  public static String buildMsgWithMap(String template, Map<String, String> map,
      List<String> notNullList) {
    // 标签内文本转换
    template = appendElementMsg(template, map);
    // 获取模板中所有需要替换参数
    List<String> list = extractTemplateParams(template);
    Map<String, String> realMap = new HashMap<>();
    // 参数过滤
    map.forEach((key, value) -> {
      if (list.contains(key)) {
        realMap.put(key, value);
      }
    });
    checkNotNullValue(realMap, notNullList);
    return replaceAllTemplateParam(template, realMap);
  }

  private static void checkNotNullValue(Map<String, String> realMap, List<String> notNullList) {
    if (CollectionUtils.isEmpty(notNullList)) {
      return;
    }
    if (CollectionUtils.isEmpty(realMap)) {
      throw new HoloinsightAlertInternalException(
          "Can not pass the not-null template field check for realMap is empty.");
    }
    for (String key : notNullList) {
      if (realMap.containsKey(key)) {
        String value = realMap.get(key);
        if (StringUtils.isEmpty(value) || value.startsWith("${")) {
          throw new HoloinsightAlertInternalException(
              "Can not pass the not-null template field check " + J.toJson(realMap) + " for key "
                  + key);
        }
      }
    }
  }

  private static String appendElementMsg(String template, Map<String, String> map) {
    for (ElementSpiEnum elementSpiEnum : ElementSpiEnum.values()) {
      if (AlarmRegexUtil.isMatch(template, AlarmConstant.IF_PATTERN)) {
        template = ElementSpiServiceFactory.getServiceByType(elementSpiEnum.getName())
            .handler(template, map);
      }
    }
    return template;
  }

  public static List<String> extractTemplateParams(String templateModel) {
    List<String> list = new ArrayList<>();
    Pattern pattern = Pattern.compile(ALARM_TEMPLATE_PATTERN);
    Matcher m = pattern.matcher(templateModel);
    while (m.find()) {
      list.add(m.group());
    }
    return list;
  }

  public static String replaceAllTemplateParam(String templateModel,
      Map<String, String> paramsMap) {

    String patternString = "\\$\\{(" + StringUtils.join(paramsMap.keySet(), '|') + ")\\}";

    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(templateModel);

    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String key = matcher.group(1);
      String value = paramsMap.get(key);
      String replacement = escapeWord(value);
      if (replacement == null) {
        LOGGER.warn("invalid {} for {}", key, J.toJson(paramsMap));
        continue;
      }
      matcher.appendReplacement(sb, replacement);
    }
    matcher.appendTail(sb);

    return sb.toString();
  }

  /**
   * escape ($()*+.[]?\^{},|)
   *
   * @param keyword
   * @return
   */

  public static String escapeWord(String keyword) {
    if (StringUtils.isNotBlank(keyword)) {
      String[] fbsArr = {"\\", "\"", "\\/"};
      for (String key : fbsArr) {
        if (keyword.contains(key)) {
          keyword = keyword.replace(key, "\\" + key);
        }
      }
      keyword = keyword.replaceAll("\\\\", "\\\\\\\\");
    }
    return keyword;

  }

}
