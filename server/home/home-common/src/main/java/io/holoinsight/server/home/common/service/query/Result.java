/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import io.holoinsight.server.common.service.Measurable;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Data
public class Result implements Measurable {

  private String metric;
  private Map<String, String> tags;
  private List<Object[]> values;

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(values)) {
      return 0;
    }
    return values.size();
  }
}
