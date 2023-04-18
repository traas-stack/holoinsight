/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: LogPattern.java, Date: 2023-04-17 Time: 12:27
 */
@Data
public class LogPattern {
  public Boolean logPattern;

  public List<LogKnownPattern> logKnownPatterns;

  // max Count of generated unknown patterns, defaults to 64
  public Integer maxUnknownPatterns = 64;
  // truncate log if length(bytes) exceed MaxLogLength, defaults to 300
  public Integer maxLogLength = 300;
}
