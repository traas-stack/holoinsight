/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.emuns;

public enum AlarmLevel {

  Urgency("1", "紧急"), Critical("2", "严重"), High("3", "高"), Medium("4", "中"), Low("5", "低");

  private final String code;
  private final String desc;

  AlarmLevel(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public static String getDesc(String code) {
    for (AlarmLevel a : AlarmLevel.values()) {
      if (a.getCode().equals(code)) {
        return a.getDesc();
      }
    }
    return null;
  }

  public String getDesc() {
    return desc;
  }
}
