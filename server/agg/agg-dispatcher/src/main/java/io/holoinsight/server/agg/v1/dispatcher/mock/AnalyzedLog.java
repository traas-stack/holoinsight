/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher.mock;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class AnalyzedLog {
  private List<LAPart> parts;
  private String sample;
  private int count;
  private List<SourceWord> sourceWords;
}
