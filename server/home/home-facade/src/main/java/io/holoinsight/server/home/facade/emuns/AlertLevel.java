/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.emuns;

public enum AlertLevel {

  Urgency("1", "紧急"), Critical("2", "严重"), High("3", "高"), Medium("4", "中"), Low("5", "低");

  private final String code;
  private final String desc;

  AlertLevel(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public static String getDesc(String code) {
    for (AlertLevel a : AlertLevel.values()) {
      if (a.getCode().equals(code)) {
        return a.getDesc();
      }
    }
    return null;
  }

  public static AlertLevel getByCode(String code) {
    for (AlertLevel a : AlertLevel.values()) {
      if (a.getCode().equals(code)) {
        return a;
      }
    }
    return null;
  }

  public static AlertLevel getByDesc(String desc) {
    for (AlertLevel a : AlertLevel.values()) {
      if (a.getDesc().equals(desc)) {
        return a;
      }
    }
    return null;
  }

  public String getDesc() {
    return desc;
  }
}
