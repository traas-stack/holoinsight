/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/10/8
 *
 * @author xzchaoo
 */
@Data
public class WindowCompleteness {
  @Getter(AccessLevel.NONE)
  private FixedSizeTags reused;

  /**
   * Table completeness infos for all related tables
   */
  @Setter(AccessLevel.NONE)
  private Map<String, TableCompleteness> tables = new HashMap<>();

  /**
   * All related targets
   */
  @Setter(AccessLevel.NONE)
  private Map<String, Map<String, Object>> allTargets = new HashMap<>();

  @JSONField(serialize = false)
  public FixedSizeTags getReusedTags() {
    reused.clearCache();
    return reused;
  }

}
