/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

import java.util.List;

import io.holoinsight.server.query.service.analysis.collect.AnalyzedLog;
import io.holoinsight.server.query.service.analysis.known.KnownValue;
import io.holoinsight.server.query.service.analysis.unknown.UnknownValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : Analysis.java, v 0.1 2022年12月08日 17:22 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Analysis {
  private int count;
  private List<AnalyzedLog> analyzedLogs;

  /**
   * The result after pre-aggregation
   */
  private KnownValue known;

  /**
   * The result after pre-aggregation
   */
  private UnknownValue unknown;
}
