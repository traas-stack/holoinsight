/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import org.apache.logging.log4j.message.ParameterizedMessage;

/**
 * @author masaimu
 * @version 2023-01-18 15:05:00
 */
public class LogMsgUtil {

  public static String getMsg(String pattern, Object... args) {
    String msg = ParameterizedMessage.format(pattern, args);
    return msg;
  }
}
