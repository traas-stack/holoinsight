/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
@Tag("it-warmup")
public class WarmupIT {
  @Test
  public void test() {
    System.out.println("warmup");
  }
}
