/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public class LogSamples {
  private int maxCount;
  private List<HostLogSample> samples;

  @Data
  public static class HostLogSample {
    private String hostname;
    /**
     * TODO List<String[]>
     */
    private LinkedList<String> logs;
  }
}
