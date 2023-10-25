/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 5:59
 */

@Data
public class LogCountResponse {

  /**
   * app name
   */
  private String app;

  /**
   * query start time :s
   */
  private Integer from;

  /**
   * query end time :s
   */
  private Integer to;

  /**
   * query log count
   */
  private Long count;

  /**
   * query context
   */
  private Map<String, String> params;
  private List<LogHistogramCount> logHistogramCounts = new ArrayList<>();
}
