/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.EnvironmentService;
import io.holoinsight.server.home.common.model.TaskEnum;

/**
 *
 * @author jinsong.yjs
 * @version 1.0: EnvironmentServiceImpl.java, v 0.1 2023年03月08日 15:50 jinsong.yjs Exp $
 */
public class DefaultEnvironmentServiceImpl implements EnvironmentService {
  @Override
  public boolean runTaskAction(TaskEnum taskName) {
    return true;
  }
}
