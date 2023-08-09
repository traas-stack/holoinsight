/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.sample;

import io.holoinsight.server.query.service.analysis.Mergeable;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LogSamples implements Mergeable, Serializable {

  private static final long serialVersionUID = 8415757482253392391L;

  private List<LogSample> samples;
  private int maxCount;

  public LogSamples() {}

  @Override
  public void merge(Mergeable other) {
    if (other == null) {
      return;
    }
    LogSamples otherLogSamples = (LogSamples) other;
    if (CollectionUtils.isEmpty(otherLogSamples.getSamples())) {
      return;
    } else if (CollectionUtils.isEmpty(this.getSamples())) {
      this.setSamples(otherLogSamples.getSamples());
    } else {
      this.getSamples().addAll(otherLogSamples.getSamples());
      int size = this.size();
      if (size > this.maxCount) {
        int count = 0;
        int[] offsets = new int[this.getSamples().size()];
        int logsPerSample = 0;
        while (count < maxCount) {
          logsPerSample++;
          for (int i = 0; i < this.getSamples().size(); i++) {
            LogSample logSample = this.getSamples().get(i);
            if (logsPerSample <= logSample.size()) {
              offsets[i] = logsPerSample;
            }
            if (++count >= maxCount) {
              break;
            }
          }
        }
        List<LogSample> newSamples = new ArrayList<>();
        for (int i = 0; i < this.getSamples().size(); i++) {
          if (offsets[i] > 0) {
            LogSample logSample = this.getSamples().get(i);
            List<String[]> newLogs = logSample.getLogs().subList(0, offsets[i]);
            newSamples.add(new LogSample(logSample.getHostname(), newLogs));
          }
        }
        this.setSamples(newSamples);
      }
    }
  }

  private int size() {
    return this.samples.stream().mapToInt(sample -> sample.getLogs().size()).sum();
  }
}
