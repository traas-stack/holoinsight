/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: AuthTargetType.java, v 0.1 2022年03月14日 5:16 下午 jinsong.yjs Exp $
 */
public enum AuthTargetType {
  SITE, // 站点
  TENANT, // 租户
  CONTEXT, // 默认上下文
  FOLDER, // 文件夹

  SRE, // 运维操作

  ;

  public static List<AuthTargetType> getSubType(AuthTargetType type) {
    List<AuthTargetType> ret = new ArrayList<>();

    if (type.equals(SITE)) {
      return Collections.singletonList(TENANT);
    }
    return ret;
  }
}
