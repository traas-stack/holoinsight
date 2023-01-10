/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import io.holoinsight.server.home.alert.model.event.AlertNotify;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/28 5:36 下午
 */
public interface AlertHandlerExecutor {

  void handle(List<AlertNotify> alarmNotifies);

}
