/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
@Data
public class CompletenessConfig {
  private Mode mode = Mode.NONE;

  /**
   * Used when mode is {@link Mode#DATA}
   */
  private String dimTable = "%s_server";

  private String targetKey = "hostname";

  /**
   * Group the completeness results according to this field, which is commonly used, for example, to
   * group by IDC attributes.
   */
  private GroupBy groupBy;

  /**
   * If this field is non-empty, these attributes will be retained for targets that fail to collect
   * or are missing.
   */
  private List<String> keepTargetKeys;

  public enum Mode {
    NONE,
    /**
     * Build completeness info by info extracted from data itself
     */
    DATA,

    /**
     * Build completeness info by a specialized completeness event
     * 
     * @see {@link io.holoinsight.server.agg.v1.core.conf.AggTaskValueTypes#COMPLETENESS_INFO}
     */
    COMPLETENESS_INFO
  }
}
