/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 5:28
 */

@Data
public class LogCountRequest {

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
  private ArrayList<String> hosts = new ArrayList<>();

  /**
   * query context
   */
  private Map<String, String> context = new HashMap<>();

  /**
   * type
   */
  private String type;
}
