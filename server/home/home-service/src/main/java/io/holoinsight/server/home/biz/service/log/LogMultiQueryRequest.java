/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 8:31
 */

@Data
public class LogMultiQueryRequest {

  /**
   * query start time :s
   */
  private Integer from;

  /**
   * query end time :s
   */
  private Integer to;

  /**
   * query info
   */
  private String query;

  /**
   * multiquery info always contains path, host, etc...
   */
  private List<SingleAppLogQuery> logQueries;

  /**
   * query context
   */
  private Map<String, String> context;

  /**
   * queried log count for the all apps suggest be avg by app count
   */
  private Integer defaultLineCount = 500;

  /**
   * order by time
   */
  private Boolean isReverse = true;

  @Data
  public static class SingleAppLogQuery {

    /**
     * app name
     */
    private String app;

    /**
     * log paths list
     */
    private List<String> paths;

    /**
     * hostname or podname list
     */
    private List<String> hosts;

    /**
     * query params
     */
    private Map<String, String> params;
  }
}
