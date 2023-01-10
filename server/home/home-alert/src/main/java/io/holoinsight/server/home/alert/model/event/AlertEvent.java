/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.alert.model.emuns.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/7/13 8:05 下午
 */
@Data
@AllArgsConstructor
public class AlertEvent {

  EventTypeEnum eventTypeEnum;

  List<AlertNotify> alarmNotifies;
}
