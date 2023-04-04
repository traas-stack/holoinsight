/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jiwliu
 * @date 2023/3/8
 */

@Data
@AllArgsConstructor
public class Endpoint {
  private String host;
  private int port;

}
