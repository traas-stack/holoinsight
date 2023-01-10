/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

/**
 * 错误码配置字段基本类型
 */
public enum OperateTypeEnum {

  EQUALS_TYPE("Equals", "Equals"), IN_TYPE("In", "In"), NOT_EQUALS_TYPE("Not_Equals",
      "Not_Equals"), NOT_IN_TYPE("Not_In", "Not_In");


  private String code;
  private String value;

  OperateTypeEnum(String code, String value) {
    this.code = code;
    this.value = value;
  }

  /**
   * 根据字符串获取枚举类
   *
   * @param name
   * @return
   */
  public static OperateTypeEnum getOperateTypeByValue(String name) {
    for (OperateTypeEnum operateTypeEnum : values()) {
      if (name.equals(operateTypeEnum.getValue())) {
        return operateTypeEnum;
      }
    }
    return null;
  }


  /**
   * 根据字符串获取枚举类
   *
   * @param name
   * @return
   */
  public static OperateTypeEnum getOperateTypeByCode(String name) {
    for (OperateTypeEnum operateTypeEnum : values()) {
      if (name.equals(operateTypeEnum.getCode())) {
        return operateTypeEnum;
      }
    }
    return null;
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
