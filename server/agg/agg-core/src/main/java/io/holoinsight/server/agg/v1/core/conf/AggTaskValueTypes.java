/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
public final class AggTaskValueTypes {
  public static final int DATA = 0;
  public static final int PUSH_EVENT_TIMESTAMP = 1;
  public static final int COMPLETENESS_INFO = 2;

  private AggTaskValueTypes() {}
}
