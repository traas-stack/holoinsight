/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: PowerConstants.java, v 0.1 2022年03月14日 5:16 下午 jinsong.yjs Exp $
 */
public enum PowerConstants {
  SUPER_ADMIN(0), // 监控自己的超高级权限
  SUPER_VIEW(20), // 同上

  // 租户相关权限
  OWNER(9), ADMIN(10), EDIT(11), VIEW(12),

  TOKEN(40), // token 权限

  NO_AUTH(100); // 无权限

  int power = 0;

  PowerConstants(int power) {
    this.power = power;
  }

  /**
   * power Enough, 当前操作权限低于等于已经具有的权限，证明有权限
   */
  public boolean powerEnough(PowerConstants p) {
    return this.power >= p.power;
  }

  /**
   * power Enough
   */
  public boolean powerEnough(Set<PowerConstants> ps) {
    if (ps == null || ps.size() == 0) {
      return false;
    }
    boolean powerRes = false;
    for (PowerConstants p : ps) {
      powerRes = powerRes || powerEnough(p);
    }
    return powerRes;
  }

}
