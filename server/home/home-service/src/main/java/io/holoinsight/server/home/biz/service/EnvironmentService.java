/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.common.model.TaskEnum;

/**
 *
 * @author jinsong.yjs
 * @version 1.0: EnvironmentService.java, v 0.1 2023年03月08日 15:49 jinsong.yjs Exp $
 */
public interface EnvironmentService {
  boolean runTaskAction(TaskEnum taskName);
}
