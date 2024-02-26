/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
public class AggTask {
  public transient long epoch;
  private long id;
  /**
   * agg task id
   */
  @Nonnull
  private String aggId;
  /**
   * agg task version
   */
  private long version;
  /**
   * agg task partitions
   */
  @Nullable
  private List<PartitionKey> partitionKeys;
  @Nonnull
  private Select select;
  @Nonnull
  private From from;
  /**
   * where that can be executed at Gateway layer
   */
  @Nullable
  private Where gatewayWhere;
  @Nullable
  private Where where;
  @Nonnull
  private GroupBy groupBy;
  @Nonnull
  private Window window;
  @Nonnull
  private Output output;

  /**
   * Agg task state config
   */
  private State state = new State();

  /**
   * Agg task fill-zero config
   */
  private FillZero fillZero = new FillZero();

  private Extension extension = new Extension();

  public AggTask() {}

  public boolean hasGroupBy() {
    return groupBy != null && !groupBy.isEmpty();
  }
}
