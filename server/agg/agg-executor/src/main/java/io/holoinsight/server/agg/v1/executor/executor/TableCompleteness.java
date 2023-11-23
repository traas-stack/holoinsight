/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * created at 2023/10/8
 *
 * @author xzchaoo
 */
@Data
@NoArgsConstructor
public class TableCompleteness {
  private String table;
  private Map<FixedSizeTags, Group> groups = new HashMap<>();
  @Setter(AccessLevel.NONE)
  private Set<String> processed = new HashSet<>();

  public TableCompleteness(String table) {
    this.table = table;
  }

  @Data
  @NoArgsConstructor
  public static class Group {
    private FixedSizeTags tags;
    private int total;
    private Map<String, Map<String, Object>> pending = new HashMap<>();
    private Map<String, Map<String, Object>> error = new HashMap<>();

    public Group(FixedSizeTags tags) {
      this.tags = tags;
    }
  }
}
