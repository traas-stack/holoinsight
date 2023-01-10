/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * created at 2022/5/13
 *
 * @author xzchaoo
 */
public class NetUtils {
  /** Constant <code>LOCAL_IP</code> */
  public static final String LOCAL_IP;

  static {
    try {
      LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * getLocalIp.
   * </p>
   */
  public static String getLocalIp() {
    return LOCAL_IP;
  }
}
