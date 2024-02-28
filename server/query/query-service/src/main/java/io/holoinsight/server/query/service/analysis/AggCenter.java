/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections.CollectionUtils;

import com.google.gson.reflect.TypeToken;

import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.query.service.analysis.collect.AnalyzedLog;
import io.holoinsight.server.query.service.analysis.collect.MergeData;
import io.holoinsight.server.query.service.analysis.known.KnownValue;
import io.holoinsight.server.query.service.analysis.unknown.UnknownValue;
import io.holoinsight.server.query.service.sample.LogSample;
import io.holoinsight.server.query.service.sample.LogSamples;

public class AggCenter {
  private static final Map<String, BiFunction<Map<String, String>, String, Mergeable>> AGGREGATORS =
      new HashMap();

  static {
    AGGREGATORS.put("unknown-analysis", (tags, json) -> {
      Analysis analysis = GsonUtils.fromJson(json, Analysis.class);

      // If the pre-aggregated result is not empty, it is returned directly.
      if (analysis != null && analysis.getUnknown() != null) {
        return analysis.getUnknown();
      }
      UnknownValue value = new UnknownValue();
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

    AGGREGATORS.put("known-analysis", (tags, json) -> {
      Analysis analysis = GsonUtils.fromJson(json, Analysis.class);
      // If the pre-aggregated result is not empty, it is returned directly.
      if (analysis != null && analysis.getKnown() != null) {
        return analysis.getKnown();
      }
      KnownValue value = null;
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

    AGGREGATORS.put("sample", (tags, json) -> {
      LogSamples logSamples = GsonUtils.fromJson(json, new TypeToken<LogSamples>() {}.getType());
      if (logSamples != null && CollectionUtils.isNotEmpty(logSamples.getSamples())) {
        List<LogSample> lss = logSamples.getSamples();
        lss.forEach(ls -> ls.setHostname(tags.getOrDefault("hostname", "UNKNOWN")));
      }
      return logSamples;
    });
  }

  public static boolean isAggregator(String aggregator) {
    return AGGREGATORS.containsKey(aggregator);
  }

  public static Mergeable parseMergeable(Map<String, String> tags, String json, String aggregator) {
    if (!isAggregator(aggregator)) {
      return null;
    } else {
      return AGGREGATORS.get(aggregator).apply(tags, json);
    }
  }
}
