/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 7:06
 */

@Data
public class Log {

  /**
   * app name
   */
  private String app;

  /**
   * log path
   */
  private String logPath;

  /**
   * log detail
   */
  private String content;
  private Map<String, Object> contentMap;

  /**
   * log collected time
   */
  private Integer logTime;

  /**
   * log tags
   */
  private Map<String, String> tags = new HashMap<>();
  private Map<String, String> packs = new HashMap<>();
}
