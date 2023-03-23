/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import io.holoinsight.server.common.service.Measurable;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class QueryResponse implements Measurable {

  private List<Result> results;

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(results)) {
      return 0;
    }
    long size = 0;
    if (!CollectionUtils.isEmpty(results)) {
      for (Result result : results) {
        size += result.measure();
      }
    }
    return size;
  }
}
