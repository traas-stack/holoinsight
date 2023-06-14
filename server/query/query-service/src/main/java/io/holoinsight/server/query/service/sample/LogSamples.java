/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.sample;

import io.holoinsight.server.query.service.analysis.Mergable;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LogSamples implements Mergable, Serializable {

  private static final long serialVersionUID = 8415757482253392391L;

  private List<LogSample> samples;
  private int maxCount;

  public LogSamples() {}

  @Override
  public void merge(Mergable other) {
    if (other == null) {
      return;
    }
    LogSamples otherLogSamples = (LogSamples) other;
    if (CollectionUtils.isEmpty(otherLogSamples.getSamples())) {
      return;
    } else if (CollectionUtils.isEmpty(this.getSamples())) {
      this.setSamples(otherLogSamples.getSamples());
    } else {
      double thisSize = this.getSamples().size();
      double otherSize = otherLogSamples.getSamples().size();
      if (thisSize > maxCount) {
        this.setSamples(this.getSamples().subList(0, maxCount));
        thisSize = maxCount;
      }
      if (otherSize > maxCount) {
        otherLogSamples.setSamples(otherLogSamples.getSamples().subList(0, maxCount));
        otherSize = maxCount;
      }
      double totalSize = thisSize + otherSize;
      List<LogSample> newSamples = new ArrayList<>();
      if (totalSize <= maxCount) {
        newSamples.addAll(this.getSamples());
        newSamples.addAll(otherLogSamples.getSamples());
      } else {
        // 按比例分配？
        // double newThisSize = thisSize / totalSize * thisSize;
        double newThisSize = thisSize / totalSize * maxCount;
        int newThisSizeInt =
            (int) (thisSize > otherSize ? Math.floor(newThisSize) : Math.ceil(newThisSize));
        int newOtherSizeInt = maxCount - newThisSizeInt;
        List<LogSample> newThisSamples = this.getSamples().subList(0, newThisSizeInt);
        List<LogSample> newOtherSamples = otherLogSamples.getSamples().subList(0, newOtherSizeInt);
        newSamples.addAll(newThisSamples);
        newSamples.addAll(newOtherSamples);
      }
      this.setSamples(newSamples);
    }
  }
}
