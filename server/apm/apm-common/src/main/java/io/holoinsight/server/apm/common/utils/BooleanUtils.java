/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

public class BooleanUtils {

  public static final int TRUE = 1;
  public static final int FALSE = 0;

  public static boolean valueToBoolean(int value) {
    if (TRUE == value) {
      return true;
    } else if (FALSE == value) {
      return false;
    } else {
      throw new RuntimeException("Boolean value error, must be 0 or 1");
    }
  }

  public static int booleanToValue(Boolean booleanValue) {
    if (booleanValue) {
      return TRUE;
    } else {
      return FALSE;
    }
  }
}
