/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.common.util.scope.PowerConstants;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserAuthRsp.java, v 0.1 2022年03月15日 5:56 下午 jinsong.yjs Exp $
 */
@AllArgsConstructor
public class UserAuthScope {
  public Map<String, List<PowerConstants>> tPowers;

  public boolean superAdmin;
  public boolean superView;
}
