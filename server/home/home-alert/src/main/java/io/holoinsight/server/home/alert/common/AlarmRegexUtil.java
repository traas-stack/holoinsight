/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author wangsiyuan
 * @date 2022/4/26 5:01 下午
 */
public class AlarmRegexUtil {

  /**
   * @description: 提取模板参数
   */
  public static List<String> extractTemplateParams(String templateModel) {
    List<String> list = new ArrayList<>();
    Pattern pattern = Pattern.compile(AlarmConstant.ALARM_TEMPLATE_PATTERN);
    Matcher m = pattern.matcher(templateModel);
    while (m.find()) {
      list.add(m.group());
    }
    return list;
  }

  /**
   * @description: 替换模板参数
   */
  public static String replaceAllTemplateParam(String templateModel,
      Map<String, String> paramsMap) {

    String patternString = "\\#\\{(" + StringUtils.join(paramsMap.keySet(), '|') + ")\\}";

    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(templateModel);

    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, paramsMap.get(matcher.group(1)));
    }
    matcher.appendTail(sb);

    return sb.toString();
  }

  /**
   * @description: 通用正则匹配
   */
  public static boolean isMatch(String message, String patternStr) {
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(message);
    if (matcher.find()) {
      return true;
    }
    return false;
  }
}
