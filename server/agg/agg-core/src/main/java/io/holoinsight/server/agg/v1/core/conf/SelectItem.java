/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Data;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class SelectItem {
  @Nonnull
  private AggFunc agg;
  @Nonnull
  @JSONField(deserializeUsing = ElectObjectDeserializer.class)
  private Elect elect;
  @Nonnull
  private String as;
  @Nullable
  private Where where;
  // @Nullable
  // private String defaultValue;

  public SelectItem() {}

  @Data
  public static class Elect {
    private String metric;
    private String field = "value";

    public static Elect of(String s) {
      String[] ss = s.split("\\.");
      Elect e = new Elect();
      e.metric = ss[0];
      if (ss.length == 2) {
        e.field = ss[1];
      }
      return e;
    }
  }
}
