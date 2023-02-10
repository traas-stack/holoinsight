/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

/**
 * <p>
 * created at 2023/2/10
 *
 * @author xzchaoo
 */
public interface SSRFHook {
  void start();

  void stop();
}
