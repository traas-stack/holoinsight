/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.common.model.TaskEnum;

/**
 *
 * @author jsy1001de
 * @version 1.0: EnvironmentService.java, v 0.1 2023年03月08日 15:49 jsy1001de Exp $
 */
public interface EnvironmentService {
  boolean runTaskAction(TaskEnum taskName);
}
