/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import javax.annotation.Nonnull;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import lombok.Getter;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Getter
public class XAggTask {
  @Nonnull
  private final AggTask inner;
  @Nonnull
  private final XSelect select;
  @Nonnull
  private final XWhere where;
  public long epoch;

  public XAggTask(AggTask inner, XSelect select, XWhere where) {
    this.inner = inner;
    this.select = select;
    this.where = where;
  }
}
