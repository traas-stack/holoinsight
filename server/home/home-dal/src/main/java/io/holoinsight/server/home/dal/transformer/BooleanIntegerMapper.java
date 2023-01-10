/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

/**
 *
 * @author jsy1001de
 * @version 1.0: BooleanIntegerMapper.java, v 0.1 2022年03月17日 6:17 下午 jinsong.yjs Exp $
 */
public class BooleanIntegerMapper {
  public static int asInt(boolean status) {
    return status ? 1 : 0;
  }

  public static boolean asBoolean(int status) {
    return status == 1;
  }
}
