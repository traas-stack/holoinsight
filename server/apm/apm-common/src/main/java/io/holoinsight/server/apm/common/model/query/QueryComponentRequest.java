/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Data;

@Data
public class QueryComponentRequest extends Request {
  private String serviceName;
  private String address;
}
