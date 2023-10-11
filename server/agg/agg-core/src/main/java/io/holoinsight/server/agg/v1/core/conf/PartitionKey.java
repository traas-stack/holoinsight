/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/24
 *
 * @author xzchaoo
 */
@Data
public class PartitionKey {
  private String ref;
  private String as;
  private String defaultValue;
}
