/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.Map;

import lombok.Data;

/**
 * <p>
 * created at 2024/1/10
 *
 * @author xzchaoo
 */
@Data
public class JoinMeta {
  private String metaTable;

  /**
   * <code>
   *     {
   *         "app": {
   *             "type": "const",
   *             "value: "foobar"
   *         },
   *         "env": {
   *             "type": "const",
   *             "value": "PROD"
   *         },
   *         "ip": {
   *             "type": "tag",
   *             "value": "ip"
   *         }
   *     }
   * </code>
   */
  private Map<String, Value> condition;

  /**
   * When no metadata is joined, whether to discard the current data
   */
  private boolean discardIfUnmatch;

  @Data
  public static class Value {
    /**
     * <ul>
     * <li>const: {@link #value} is a const</li>
     * <li>tag: {@link #value} is a tag name</li>
     * </ul>
     */
    private String type;
    private String value;
  }
}
