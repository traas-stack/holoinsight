/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.Collections;

import org.apache.commons.collections4.CollectionUtils;

import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@NoArgsConstructor
public class LogAnalysisState {
  private static final String HOSTNAME_TAG = "hostname";

  private boolean unknownMode;

  private UnknownValue unknown;
  private KnownValue known;

  public LogAnalysisState(boolean unknownMode) {
    this.unknownMode = unknownMode;
  }

  public void add(DataAccessor da, LogAnalysis la) {
    if (la == null || CollectionUtils.isEmpty(la.getAnalyzedLogs())) {
      return;
    }
    if (unknownMode) {
      addUnknown(da, la);
    } else {
      addKnown(da, la);
    }
  }

  private void addUnknown(DataAccessor da, LogAnalysis la) {
    if (unknown == null) {
      unknown = new UnknownValue();
    }

    String hostname = da.getTag(HOSTNAME_TAG);

    int totalCount = 0;
    StringBuilder reuse = new StringBuilder();
    for (AnalyzedLog al : la.getAnalyzedLogs()) {
      totalCount += al.getCount();
      al.addIpCount(hostname, al.getCount());
      unknown.merge(reuse, al);
    }

    unknown.addIpCount(hostname, totalCount);
  }

  private void addKnown(DataAccessor da, LogAnalysis la) {
    // 'known' has only one analyzed log
    AnalyzedLog al = la.getAnalyzedLogs().get(0);

    if (known == null) {
      known = new KnownValue();
      known.setSample(al.getSample());
    }

    String hostname = da.getTag(HOSTNAME_TAG);
    known.merge(hostname, al);
  }

  public Object getFinalValue() {
    if (unknown != null) {
      unknown.update();
      return Collections.singletonMap("unknown", unknown);
    }
    if (known != null) {
      return Collections.singletonMap("known", known);
    }
    return null;
  }
}
