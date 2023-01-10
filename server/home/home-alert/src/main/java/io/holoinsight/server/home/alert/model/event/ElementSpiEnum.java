/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

/**
 * @author wangsiyuan
 * @date 2022/4/26 5:02 下午
 */
public enum ElementSpiEnum {
  IF_ELEMENT("if", "if标签");

  private String name;

  private String desc;

  ElementSpiEnum(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
