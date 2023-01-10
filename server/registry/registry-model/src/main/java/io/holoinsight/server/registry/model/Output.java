/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class Output {
  /**
   * console/gateway console 仅仅用于调试用
   */
  private String type;
  private Gateway gateway;

  @ToString
  @Getter
  @Setter
  public static class Gateway {
    /**
     * 如果非空则优先使用该字段作为metricName
     */
    private String metricName;
  }
}
