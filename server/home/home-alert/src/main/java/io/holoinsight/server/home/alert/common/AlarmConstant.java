/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

/**
 * @author wangsiyuan
 * @date 2022/4/26 5:03 下午
 */
public class AlarmConstant {
  // 空字符串
  public static final String EMPTY = "";
  // 模板正则提取
  public static final String ALARM_TEMPLATE_PATTERN = "(?<=\\#\\{).*?(?=\\})";
  // if开始标签
  public static final String IF_START_ELEMENT = "<if";
  // if结束标签
  public static final String IF_END_ELEMENT = "</if>";
  // test 属性
  public static final String TEST_PROPERTY = "test";
  // if标签表达式
  public static final String IF_PATTERN = "(?=<if)([\\s\\S]*?)(?=</if>)";
}
