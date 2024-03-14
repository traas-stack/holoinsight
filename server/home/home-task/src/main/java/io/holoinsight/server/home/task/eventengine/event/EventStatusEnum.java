/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

/**
 *
 * @author jsy1001de
 * @version 1.0: EventStatusEnum.java, v 0.1 2022年04月07日 11:37 上午 jinsong.yjs Exp $
 */
public enum EventStatusEnum {
  /**
   * 新建事件，未处理
   */
  NEW("NEW", "未处理"),

  /**
   * 处理成功
   */
  SUCCESS("SUCCESS", "处理成功"),

  /**
   * 处理失败
   */
  FAILED("FAILED", "处理失败");

  /**
   * 枚举值
   */
  private String code;

  /**
   * 描述
   */
  private String desc;

  /**
   * @param code
   * @param desc
   */
  EventStatusEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static EventStatusEnum getEnumByCode(String code) {

    if (code != null && !"".equals(code.trim())) {

      for (EventStatusEnum e : EventStatusEnum.values()) {
        if (code.equals(e.getCode())) {
          return e;
        }
      }
    }

    return null;
  }

  /**
   * Getter method for property code.
   *
   * @return property value of code
   */
  public String getCode() {
    return code;
  }

  /**
   * Getter method for property desc.
   *
   * @return property value of desc
   */
  public String getDesc() {
    return desc;
  }
}
