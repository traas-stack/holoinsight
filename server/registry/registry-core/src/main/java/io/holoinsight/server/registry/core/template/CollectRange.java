/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.util.List;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.registry.core.utils.Dict;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2022/3/2
 *
 * @author zzhb101
 */
@Getter
@Setter
public class CollectRange {
  @Deprecated
  public static final String DIM = "dim";
  public static final String CLOUDMONITOR = "cloudmonitor";
  public static final String CENTRAL = "central";
  public static final String NONE = "none";

  /**
   * 采集范围使用什么来描述
   */
  private String type;
  private Cloudmonitor cloudmonitor;

  public void reuseStrings() {
    type = Dict.get(type);
  }

  @Data
  public static class Cloudmonitor {
    String table;
    List<QueryExample> ranges;
  }
}
