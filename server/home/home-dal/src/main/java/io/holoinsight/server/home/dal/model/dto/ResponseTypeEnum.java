/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

/**
 * 配置http错误码时的枚举类，代表从何处取错误码
 */
public enum ResponseTypeEnum {

  /**
   * Return返回枚举
   */
  RETURN("Return", "Return"),

  /**
   * ModelMap枚举
   */
  MODEL_MAP("ModelMap", "ModelMap"),
  /**
   * 默认枚举
   */
  DEFAULT("Default", "Default");


  private String code;
  private String value;

  ResponseTypeEnum(String code, String value) {
    this.code = code;
    this.value = value;
  }

  /**
   * Getter method for property <tt>code</tt>.
   *
   * @return property value of code
   */
  public String getCode() {
    return code;
  }

  /**
   * Setter method for property <tt>counterType</tt>.
   *
   * @param code value to be assigned to property code
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Getter method for property <tt>value</tt>.
   *
   * @return property value of value
   */
  public String getValue() {
    return value;
  }

  /**
   * Setter method for property <tt>counterType</tt>.
   *
   * @param value value to be assigned to property value
   */
  public void setValue(String value) {
    this.value = value;
  }
}
