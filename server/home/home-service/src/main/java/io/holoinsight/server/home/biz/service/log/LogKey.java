/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-13 1:55
 */

@Data
public class LogKey {

  /**
   * app name
   */
  private String app;

  /**
   * log path
   */
  private String path;

  /**
   * key params
   */
  private Map<String, String> params;
}
