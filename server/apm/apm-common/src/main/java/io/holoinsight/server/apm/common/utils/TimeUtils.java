/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

public class TimeUtils {

  public static long unixNano2MS(long unixNano) {
    return unixNano / 1000000;
  }
}
