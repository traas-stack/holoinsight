/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2023/8/23
 *
 * @author xzchaoo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlsLogstore {
  private String endpoint;
  private String project;
  private String logstore;
  private String ak;
  private String sk;
}
