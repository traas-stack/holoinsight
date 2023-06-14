/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.sample;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogSample implements Serializable {

  private static final long serialVersionUID = -7744949698357759207L;

  private String hostname;
  private String log;

  public LogSample() {}

}
