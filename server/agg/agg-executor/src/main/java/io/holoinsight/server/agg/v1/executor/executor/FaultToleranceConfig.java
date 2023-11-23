/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.time.Duration;

import lombok.Data;

/**
 * Fault Tolerance Config
 * <p>
 * created at 2023/9/25
 *
 * @author xzchaoo
 */
@Data
public class FaultToleranceConfig {
  private Type type = Type.RECOMPUTE;

  /**
   * Minimum interval for saving offsets (according to event time)
   */
  private Duration saveOffsetsInterval = Duration.ofSeconds(30);

  /**
   * Like maxOutOfOrderness in Flink
   */
  private Duration maxOutOfOrderness = Duration.ofSeconds(30);

  private StateConfig stateConfig = new StateConfig();

  /**
   * Save state interval
   */
  private Duration saveStateInterval = Duration.ofMinutes(1);

  private Duration maxLagTime = Duration.ofHours(2);

  private boolean registerShutdownHook;

  public enum Type {
    /**
     * No fault tolerance
     */
    NONE,
    /**
     * Tolerate faults through state storing and loading
     */
    STATE_SAVE,
    /**
     * Tolerate faults through recomputing
     */
    RECOMPUTE,
  }
}
