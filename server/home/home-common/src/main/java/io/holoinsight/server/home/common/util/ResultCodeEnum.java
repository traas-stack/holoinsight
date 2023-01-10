/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

/**
 *
 * @author jsy1001de
 * @version 1.0: ResultCode.java, v 0.1 2022年03月15日 12:24 下午 jinsong.yjs Exp $
 */
public enum ResultCodeEnum {
  /**
   * 成功
   */
  SUCCESS("SUCCESS", "成功"),
  /**
   * 非法参数
   */
  PARAMETER_ILLEGAL("PARAMETER_ILLEGAL", "非法参数"),

  /**
   * 键重复
   */
  DUPLICATE_KEY("DUPLICATE_KEY", "键重复"),

  /**
   * 状态不合法
   */
  STATUS_ILLEGAL("STATUS_ILLEGAL", "状态不合法"),

  /**
   * 没找到相应记录
   */
  CANNOT_FIND_RECORD("CANNOT_FIND_RECORD", "没找到相应记录"),

  /**
   * 反射异常
   */
  REFLECT_ERROR("REFLECT_ERROR", "反射异常"),

  /**
   * 数据库异常
   */
  DATAACCESS_ERROE("DATAACCESS_ERROE", "数据库操作异常"),

  /**
   * 系统异常
   */
  SYSTEM_ERROR("SYSTEM_ERROR", "系统异常"),

  MONITOR_SYSTEM_ERROR("MONITOR_SYSTEM_ERROR", "Monitor系统异常"),

  /** 操作失败 */
  FAILED("FAILED", "操作失败"),

  NO_AUTH("NO_AUTH", "没权限"),

  /**
   * 对象转换出错
   */
  OBJECT_CONVERT_ERROR("OBJECT_CONVERT_ERROR", "对象转换出错"),

  EXCEED_SERIES_LIMIT("EXCEED_SERIES_LIMIT", "超过查询限制"),;

  /**
   * 错误码
   */
  private final String resultCode;

  /**
   * 错误信息
   */
  private final String resultMessage;

  /**
   * 创建一个ErrorCodeEnum
   * 
   * @param errorCode 错误码
   * @param errorMessage 错误信息
   */
  private ResultCodeEnum(String errorCode, String errorMessage) {
    this.resultCode = errorCode;
    this.resultMessage = errorMessage;
  }

  /**
   * Getter method for property <tt>resultCode</tt>.
   *
   * @return property value of resultCode
   */
  public String getResultCode() {
    return resultCode;
  }

  /**
   * Getter method for property <tt>resultMessage</tt>.
   *
   * @return property value of resultMessage
   */
  public String getResultMessage() {
    return resultMessage;
  }
}
