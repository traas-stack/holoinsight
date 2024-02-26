/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
@NoArgsConstructor
public class AggFunc {
  public static final int TYPE_SUM = 1;
  public static final int TYPE_AVG = 2;
  public static final int TYPE_COUNT = 3;
  public static final int TYPE_MIN = 4;
  public static final int TYPE_MAX = 5;

  public static final int TYPE_LOGSAMPLES_MERGE = 6;
  public static final int TYPE_LOGANALYSIS_MERGE = 7;

  public static final int TYPE_TOPN = 8;
  public static final int TYPE_AVG_MERGE = 9;
  public static final int TYPE_HLL = 10;
  public static final int TYPE_PERCENTILE = 11;

  private static final Map<String, Integer> TYPE_TO_INT = new HashMap<>();

  static {
    TYPE_TO_INT.put("SUM", 1);
    TYPE_TO_INT.put("AVG", 2);
    TYPE_TO_INT.put("COUNT", 3);
    TYPE_TO_INT.put("MIN", 4);
    TYPE_TO_INT.put("MAX", 5);

    TYPE_TO_INT.put("LOGSAMPLES_MERGE", 6);
    TYPE_TO_INT.put("LOGANALYSIS_MERGE", 7);

    TYPE_TO_INT.put("TOPN", 8);
    TYPE_TO_INT.put("AVG_MERGE", 9);
    TYPE_TO_INT.put("HLL", 10);
    TYPE_TO_INT.put("PERCENTILE", 11);
  }

  private String type;
  private transient int typeInt;

  @Nullable
  private TopnParams topn;

  @Nullable
  private LogSamplesMergeParams logSamplesMerge;

  public AggFunc(String type) {
    this.type = type;
  }

  public int getTypeInt() {
    if (typeInt != 0) {
      return typeInt;
    }
    typeInt = TYPE_TO_INT.getOrDefault(type, 0);
    return typeInt;
  }

  @Data
  public static class TopnParams {
    /**
     * Order by which field
     */
    private String orderBy;
    /**
     * Desc or asc
     */
    private boolean asc;
    /**
     * topn limit
     */
    private int limit = 3;
  }

  @Data
  public static class LogSamplesMergeParams {
    /**
     * <ul>
     * <li>ANY</li>
     * <li>HOSTNAME</li>
     * </ul>
     */
    @Nullable
    private String strategy = "ANY";
  }

}
