/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/1
 *
 * @author xzchaoo
 */
@Slf4j
@Data
public class MergedCompleteness {
  public static final int MAX_FAILURE_RECORD_COUNT = 3;
  /**
   * ok count
   */
  public int ok;
  /**
   * expected total count
   */
  public int total;
  public List<GroupCompleteness> details = new ArrayList<>();

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient Map<Map<String, String>, GroupCompleteness> detailsMap = new HashMap<>();

  @Data
  public static class GroupCompleteness {
    private Map<String, String> tags;
    private Map<String, TableCompleteness> tables = new HashMap<>();

    public GroupCompleteness() {}

    public GroupCompleteness(Map<String, String> tags) {
      this.tags = tags;
    }

  }

  @Data
  public static class TableCompleteness {
    private int ok;
    private int total;
    private int error;
    private int missing;
    private List<Target> errorTargets;
    private List<Target> missingTargets;

    public void addError(Target target) {
      if (errorTargets == null) {
        errorTargets = new LinkedList<>();
      }
      errorTargets.add(target);
    }

    public void addMissing(Target target) {
      if (missingTargets == null) {
        missingTargets = new LinkedList<>();
      }
      missingTargets.add(target);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Target {
    private String key;
    private Map<String, Object> target;
  }
}
