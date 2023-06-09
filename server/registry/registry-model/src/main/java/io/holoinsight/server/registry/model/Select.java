/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class Select {
  private List<SelectItem> values = new ArrayList<>();

  private LogSamples logSamples;

  /**
   * select agg($elect) as ...
   */
  @ToString
  @Getter
  @Setter
  public static class SelectItem {
    private String as;
    /**
     * 字段的提取方式, 当agg==count时该字段为null
     */
    private Elect elect;
    /**
     * 聚合方法 sum/min/max/avg/count
     */
    private String agg;
  }

  @ToString
  @Getter
  @Setter
  public static class LogSamples {
    private Boolean enabled;
    private Where where;
    private Integer maxCount;
    private Integer maxLength;
  }
}
