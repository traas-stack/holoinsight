/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import lombok.Data;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/14 11:45 上午
 */
@Data
public class NotifyDataInfo {

  private String metric; // 监控项

  private Map<String, String> tags;

  private String triggerContent; // 触发内容简述

  private Double currentValue; // 当前时间的值

  private String msg;// 告警消息
}
