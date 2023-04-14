/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 8:49
 */

@Data
public class LogMultiQueryResponse {

  /**
   * log details
   */
  private List<Log> logs;

  /**
   * query info
   */
  private String query;

  /**
   * query start time :s
   */
  private Integer from;

  /**
   * query end time :s
   */
  private Integer to;

  /**
   * query context
   */
  private Map<String, String> context;

  /**
   * multi query context list
   */
  private List<LogMultiQueryRequest.SingleAppLogQuery> logQueries;
}
