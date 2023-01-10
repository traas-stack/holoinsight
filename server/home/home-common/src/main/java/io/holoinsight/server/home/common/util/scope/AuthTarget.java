/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: AuthTarget.java, v 0.1 2022年03月14日 5:15 下午 jinsong.yjs Exp $
 */
@Data
public class AuthTarget {
  public AuthTargetType authTargetType;
  public String quthTargetId;

  public AuthTarget(AuthTargetType authTargetType, String quthTargetId) {
    super();
    this.authTargetType = authTargetType;
    this.quthTargetId = quthTargetId;
  }
}
