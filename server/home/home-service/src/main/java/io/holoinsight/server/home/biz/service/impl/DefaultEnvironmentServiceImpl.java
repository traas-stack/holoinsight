/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.EnvironmentService;
import io.holoinsight.server.home.common.model.TaskEnum;

/**
 *
 * @author jsy1001de Date: 2023-03-10 Time: 13:16
 */
public class DefaultEnvironmentServiceImpl implements EnvironmentService {
  @Override
  public boolean runTaskAction(TaskEnum taskName) {
    return true;
  }
}
