/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.common;

/**
 * @author masaimu
 * @version 2023-08-27 19:17:00
 */
public enum DashboardType {
  magi("magi"), iot("iot"), miniapp("miniapp"), faas("faas");

  private final String code;

  DashboardType(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }
}
