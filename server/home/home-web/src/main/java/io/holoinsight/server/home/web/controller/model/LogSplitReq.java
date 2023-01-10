/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: LogSplitReq.java, v 0.1 2022年04月08日 4:47 下午 jinsong.yjs Exp $
 */
@Data
public class LogSplitReq {
  private String splitType;
  private String seperators;
  private String cols;
  private List<String> sampleLogs;
  private String script;
}
