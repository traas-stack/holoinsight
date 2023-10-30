/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2023/9/28
 *
 * @author xzchaoo
 */
public interface XOutput {
  String type();

  void write(Batch batch);

  @Data
  class Batch {
    public AggTaskKey key;
    public OutputItem oi;
    public WindowInfo window;
    public List<Group> groups = new ArrayList<>();

    public Batch() {}

    public Batch(AggTaskKey key, OutputItem oi, WindowInfo window) {
      this.key = key;
      this.oi = oi;
      this.window = window;
    }
  }

  @Data
  @AllArgsConstructor
  class WindowInfo {
    public long timestamp;
    public MergedCompleteness mergedCompleteness;
    public WindowStat stat;
    public boolean preview;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class Group {
    public FixedSizeTags tags;
    public Map<String, Object> finalFields;
  }

}
