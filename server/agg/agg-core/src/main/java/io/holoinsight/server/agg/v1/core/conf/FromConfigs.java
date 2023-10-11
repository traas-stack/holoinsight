/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.Set;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public class FromConfigs {
  private Set<String> tableNames;
}
