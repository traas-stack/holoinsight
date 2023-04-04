/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import lombok.Data;

/**
 * @author jiwliu
 * @date 2023/1/11
 */
@Data
public class QueryMetricsParam {

  String tenant;
  String name;
  Integer limit;
}
