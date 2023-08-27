/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */

package io.holoinsight.server.home.web.common;

/**
 * @author masaimu
 * @version 2023-08-27 19:17:00
 */
public enum DashboardType {
  magi("magi"), iot("iot"), miniapp("miniapp");

  private final String code;

  DashboardType(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }
}
