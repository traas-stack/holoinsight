/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 5:28
 */

@Data
public class LogQueryRequest {

  /**
   * app name
   */
  private String app;

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
   * log path
   */
  private String logPath;

  /**
   * hostname or podname list
   */
  private ArrayList<String> instances;

  /**
   * query context
   */
  private Map<String, String> context;

  /**
   * page num
   */
  private Integer pageNum;

  /**
   * page size
   */
  private Integer pageSize;

  /**
   * order by time
   */
  private Boolean isReverse = true;

  /**
   * type
   */
  private String type;
}
