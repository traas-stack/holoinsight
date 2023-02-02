/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 参数检测工具类
 * 
 * @author jsy1001de
 * @version 1.0: ParaCheckUtil.java, v 0.1 2022年03月15日 1:14 下午 jinsong.yjs Exp $
 */
public class ParaCheckUtil {
  private static final Pattern PATTERN = Pattern.compile("^[a-z][a-z0-9\\-]*[a-z0-9]$");

  private static final Pattern PATTERN_NEW = Pattern.compile("^[a-z]{1,20}");

  private static final Pattern PATTERN_SYSTEM = Pattern.compile("^[a-z]{1,20}");

  private static final Pattern PATTERN_CLUSTER = Pattern.compile("^[a-z][a-z0-9\\-]{1,20}");

  private static final Pattern PATTERN_APPLICATION = Pattern.compile("^[a-z]{1,20}");

  private static final Pattern PATTERN_AIG_NAME =
      Pattern.compile("^[a-z]{1,20}-[a-z][a-z0-9]{0,27}");

  /// **
  // * 检测对象类型
  // *
  // * @param object 对象
  // */
  // public static void checkParaNotNull(Object object) {
  // if (object == null) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测对象类型
   *
   * @param object 对象
   */
  public static void checkParaNotNull(Object object, String errorMsg) {
    if (object == null) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /**
   * 检测 end > start
   *
   * @param param 数值
   */
  public static void checkTimeRange(Long start, Long end, String errorMsg) {
    if (start > end) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 检测字符串类型
  // *
  // * @param param 字符串
  // */
  // public static void checkParaNotBlank(String param) {
  // if (StringUtils.isBlank(param)) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测字符串类型
   *
   * @param param
   * @param errorMsg
   */
  public static void checkParaNotBlank(String param, String errorMsg) {
    if (StringUtils.isBlank(param)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }


  public static void checkParaStartWith(String param, String startString, String errorMsg) {
    if (StringUtils.isBlank(param) || !param.startsWith(startString)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 检测数值不是负数
  // *
  // * @param param 数值
  // */
  // public static void checkParaNotNegative(Integer param) {
  // if (param == null || param < 0) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测数值不是负数
   *
   * @param param 数值
   */
  public static void checkParaNotNegative(Integer param, String errorMsg) {
    if (param == null || param < 0) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 检测数值是正数
  // *
  // * @param param
  // */
  // public static void checkParaPositive(Integer param) {
  // if (param == null || param <= 0) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /// **
  // * 检测集合类型
  // *
  // * @param collection 集合
  // */
  // public static void checkParaNotEmpty(Collection<?> collection) {
  // if (CollectionUtils.isEmpty(collection)) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测集合类型
   *
   * @param collection 集合
   */
  public static void checkParaNotEmpty(Collection<?> collection, String errorMsg) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 检测集合类型
  // *
  // * @param collection 集合
  // */
  // public static void checkParaNotEmpty(Map map) {
  // if (CollectionUtils.isEmpty(map)) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测集合类型
   *
   * @param collection 集合
   */
  public static void checkParaNotEmpty(Map map, String errorMsg) {
    if (CollectionUtils.isEmpty(map)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 检测数组类型
  // *
  // * @param array 数组
  // */
  // public static void checkParaNotEmpty(String[] array) {
  // if (ArrayUtils.isEmpty(array)) {
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL);
  // }
  // }

  /**
   * 检测数组类型
   *
   * @param array 数组
   */
  public static void checkParaNotEmpty(String[] array, String errorMsg) {
    if (ArrayUtils.isEmpty(array)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /**
   * 检查两个对象是否相等
   *
   * @param obj1
   * @param obj2
   */
  public static void checkEquals(Object obj1, Object obj2, String errorMsg) {
    if (!ObjectUtils.equals(obj1, obj2)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  /// **
  // * 应用名称由小写字母，数字和中横线组成，必须字母开头，最大32个字符
  // *
  // * @param appname
  // */
  // public static void checkAppname(String appname) {
  // Pattern pattern = Pattern.compile("^[a-z][0-9a-z-]{5,31}$");
  // Matcher matcher = pattern.matcher(appname);
  // boolean result = matcher.matches(); //当条件满足时，将返回true，否则返回false
  // if (!result) {
  // String checkMessage = "应用名[" + appname + "]不符合长域名规范，格式：小写字母，数字和中横线组成，必须字母开头，最大32个字符";
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, checkMessage);
  // }
  // }
  //
  /// **
  // * 检查主机名是否为长域名
  // * 例如：foo-60-10.foo.foo.com
  // *
  // * @param hostname
  // * @return 如果返回值为null，则说明服务器主机名验证无误，反之则有误，且返回值即错误信息
  // */
  // public static void checkHostnameStyle(List<String> hostnames) {
  // StringBuilder sb = null;
  // if (!CollectionUtils.isEmpty(hostnames)) {
  // for (String hostname : hostnames) {
  // if (!isHostnameCorrect(hostname)) {
  // if (sb == null) {
  // sb = new StringBuilder();
  // sb.append("服务器主机名[");
  // }
  // sb.append(hostname + ",");
  // }
  // }
  // }
  // if (sb != null) {
  // String checkMessage = sb.substring(0, sb.length() - 1)
  // + "]不符合长域名规范，格式如：foo-60-10.foo.foo.com";
  // throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, checkMessage);
  // }
  // }
  //
  /// **
  // * 长域名格式xxx-数字-数字.xxx.xxx.xxx
  // *
  // * @param hostname
  // * @return
  // */
  // private static boolean isHostnameCorrect(String hostname) {
  // if (StringUtils.isNotBlank(hostname)) {
  // Pattern pattern = Pattern
  // .compile("[a-zA-Z]{1,}-[0-9]{1,}-[0-9]{1,}\\.[a-zA-Z]{1,}\\.[a-zA-Z]{1,}\\.[a-zA-Z]{1,}");
  // Matcher matcher = pattern.matcher(hostname);
  // if (!matcher.find()) {
  // return false;
  // } else {
  // return true;
  // }
  // } else {
  // return false;
  // }
  // }

  /**
   * 判断boolean, false直接报错
   * 
   * @param result
   * @param errorMsg
   */
  public static void checkParaBoolean(Boolean result, String errorMsg) {
    if (!result) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }
}
