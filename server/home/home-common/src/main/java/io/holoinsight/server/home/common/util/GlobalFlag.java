/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: GlobalFlag.java, v 0.1 2022年08月04日 7:28 下午 jinsong.yjs Exp $
 */
public class GlobalFlag {

  private static final Set<String> ukFlags = new HashSet<>();


  /**
   * 调试是否开启
   * 
   * @return 调试是否开启
   */
  public static boolean isDebugEnable(String ukFlag) {
    return ukFlags.contains(ukFlag);
  }

  /**
   * 开启调试
   */
  public static void enableDebug(String ukFlag) {
    if (StringUtils.isNotBlank(ukFlag)) {
      ukFlags.add(ukFlag);
    }
  }

  /**
   * 关闭调试
   */
  public static void disableDebug() {
    ukFlags.clear();
  }

  /**
   * 变量
   */
  public static Set<String> getUkFlags() {
    return ukFlags;
  }
}
