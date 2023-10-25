/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class OutputItem {
  /**
   * <ul>
   * <li>CONSOLE</li>
   * <li>TSDB</li>
   * </ul>
   */
  @Nonnull
  private String type;
  @Nonnull
  private String name;
  @Nonnull
  private List<OutputField> fields = new ArrayList<>();

  /**
   * specific parameters for type
   */
  @Nonnull
  private Map<String, String> params = new HashMap<>();

  private Topn topn;

  public OutputItem() {}

  @Data
  public static class Topn {
    /**
     * whether enable topn after agg
     */
    private boolean enabled;
    /**
     * topn field
     */
    private String orderBy;
    private boolean asc;
    private int limit = 3;
  }
}
