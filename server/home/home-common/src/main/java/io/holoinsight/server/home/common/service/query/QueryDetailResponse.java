/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author masaimu
 * @version 2023-08-15 12:57:00
 */
@Data
public class QueryDetailResponse {

  private List<DetailResult> results;

  public void distinct() {
    if (CollectionUtils.isEmpty(results)) {
      return;
    }
    for (DetailResult detailResult : results) {
      detailResult.distinct();
    }
  }

  @Data
  public static class DetailResult {
    private List<String> tables;
    private String sql;
    private List<String> headers;
    private List<Object[]> values;

    public void distinct() {
      if (CollectionUtils.isEmpty(values)) {
        return;
      }
      List<Object[]> distinctValues = new ArrayList<>();
      Set<Object> set = new HashSet<>();
      for (Object[] v : values) {
        if (v.length != 1) {
          distinctValues.add(v);
        } else {
          set.add(v[0]);
        }
      }
      for (Object v : set) {
        distinctValues.add(new Object[] {v});
      }
      this.values = distinctValues;
    }
  }
}
