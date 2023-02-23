/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.emuns;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:49 下午
 */
public enum TimeFilterEnum {
  DAY("days"), WEEK("weeks"), MONTH("months");


  TimeFilterEnum(String desc) {
    this.desc = desc;
  }

  private String desc;

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
