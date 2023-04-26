/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.emuns;

/**
 * @author wangsiyuan
 * @date 2022/3/17 9:58 下午
 */
public enum FunctionEnum {

  Current("Current", "当前时间值", "RULE"), //
  PeriodRate("PeriodRate", "同环比比例", "RULE"), //
  PeriodValue("PeriodValue", "同环比差值", "RULE"), //
  PeriodAbs("PeriodAbs", "同环比绝对值", "RULE"), //
  Step("Step", "周期时间比较", "RULE"), //
  ValueUp("ValueUp", "值上涨", "AI"), //
  ValueDown("ValueDown", "值下跌", "AI"), //
  AnomalyUp("AnomalyUp", "异常检测值上涨", "AI"), //
  AnomalyDown("AnomalyDown", "异常检测值下跌", "AI");

  private final String code;

  private final String desc;

  private final String type;


  FunctionEnum(String code, String desc, String type) {
    this.code = code;
    this.desc = desc;
    this.type = type;
  }

  public String getCode() {
    return code;
  }

  public String getType() {
    return type;
  }

  public String getDesc() {
    return desc;
  }
}
