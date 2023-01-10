/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

import lombok.Data;

/**
 * <p>
 * created at 2022/4/18
 *
 * @author xzchaoo
 */
@Data
public class AuthInfo {
  private String tenant;
  private boolean central;
}
