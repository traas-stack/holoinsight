/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

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
public class GroupBy {
  private int maxKeys;
  private List<Group> groups;
  /**
   * Log analysis conf
   */
  private LogAnalysis logAnalysis;

  @ToString
  @Getter
  @Setter
  public static class Group {
    private String name;
    private Elect elect;
  }

  /**
   * Log analysis conf
   */
  @ToString
  @Getter
  @Setter
  public static class LogAnalysis {
    /**
     * patterns to match, will be visited in order, break when first match
     */
    private List<LogAnalysisPattern> patterns;
    /**
     * max Count of generated unknown patterns, defaults to 64
     */
    private int maxUnknownPatterns;
    /**
     * truncate log if length(bytes) exceed MaxLogLength, defaults to 300
     */
    private int maxLogLength;
  }

  @ToString
  @Getter
  @Setter
  public static class LogAnalysisPattern {
    /**
     * pattern name
     */
    private String name;
    /**
     * 'where' predicate of pattern
     */
    private Where where;
  }
}
