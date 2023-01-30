/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

import io.holoinsight.server.query.service.analysis.collect.AnalyzedLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
