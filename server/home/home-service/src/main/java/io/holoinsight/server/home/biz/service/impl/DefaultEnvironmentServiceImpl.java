/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.EnvironmentService;
import io.holoinsight.server.home.common.model.TaskEnum;

/**
 *
 * @author jsy1001de
 * @version 1.0: EnvironmentServiceImpl.java, v 0.1 2023年03月08日 15:50 jsy1001de Exp $
 */
public class DefaultEnvironmentServiceImpl implements EnvironmentService {
  @Override
  public boolean runTaskAction(TaskEnum taskName) {
    return true;
  }
}
