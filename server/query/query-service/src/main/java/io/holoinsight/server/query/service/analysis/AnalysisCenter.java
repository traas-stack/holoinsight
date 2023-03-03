/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

import io.holoinsight.server.query.service.analysis.collect.AnalyzedLog;
import io.holoinsight.server.query.service.analysis.collect.MergeData;
import io.holoinsight.server.query.service.analysis.known.KnownValue;
import io.holoinsight.server.query.service.analysis.unknown.UnknownValue;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author xiangwanpeng
 * @version : AnalysisAggregators.java, v 0.1 2022年12月08日 16:38 xiangwanpeng Exp $
 */
public class AnalysisCenter {
  private static final Map<String, BiFunction<Map<String, String>, String, Mergable>> ANALYSIS_AGGREGATORS =
      new HashMap();

  static {
    ANALYSIS_AGGREGATORS.put("unknown-analysis", (tags, json) -> {
      UnknownValue value = new UnknownValue();
      Analysis analysis = GsonUtils.fromJson(json, Analysis.class);
      if (analysis != null && CollectionUtils.isNotEmpty(analysis.getAnalyzedLogs())) {
        List<AnalyzedLog> logs = analysis.getAnalyzedLogs();
        String host = tags.getOrDefault("hostname", "UNKNOWN");
        int count = 0;
        for (AnalyzedLog AnalyzedLog : logs) {
          count += AnalyzedLog.getCount();
          AnalyzedLog.getIpCountMap().put(host, AnalyzedLog.getCount());
        }
        MergeData MergeData = new MergeData();
        MergeData.setAnalyzedLogs(logs);
        value.setMergeData(MergeData);
        Map<String, Integer> ipCountMap = new HashMap<>();
        ipCountMap.put(host, count);
        value.setIpCountMap(ipCountMap);
      }
      return value;
    });

    ANALYSIS_AGGREGATORS.put("known-analysis", (tags, json) -> {
      KnownValue value = null;
      Analysis analysis = GsonUtils.fromJson(json, Analysis.class);
      if (analysis != null && CollectionUtils.isNotEmpty(analysis.getAnalyzedLogs())) {
        List<AnalyzedLog> logs = analysis.getAnalyzedLogs();
        AnalyzedLog log = logs.get(0);
        KnownValue that = new KnownValue();
        that.setSample(log.getSample());
        that.setCount(log.getCount());
        String host = tags.getOrDefault("hostname", "UNKNOWN");
        that.getIpCountMap().put(host, log.getCount());
        if (value == null) {
          value = that;
        } else {
          value.merge(that);
        }
      }
      return value;
    });
  }

  public static boolean isAnalysis(String aggregator) {
    return ANALYSIS_AGGREGATORS.containsKey(aggregator);
  }

  public static Mergable parseAnalysis(Map<String, String> tags, String json, String aggregator) {
    if (!isAnalysis(aggregator)) {
      return null;
    } else {
      return ANALYSIS_AGGREGATORS.get(aggregator).apply(tags, json);
    }
  }
}
