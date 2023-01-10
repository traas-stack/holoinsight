/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access.model;

/**
 *
 * @author jsy1001de
 * @version : AccessLimitedException.java, v 0.1 2022年06月10日 3:57 下午 jinsong.yjs Exp $
 */
public class AccessLimitedException extends RuntimeException {

  public AccessLimitedException(String message) {
    super(message);
  }
}
