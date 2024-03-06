/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    public void sort(String order, String... priorities) {
      if (CollectionUtils.isEmpty(headers) || CollectionUtils.isEmpty(values)) {
        return;
      }
      int index = -1;
      for (String priority : priorities) {
        for (int i = 0; i < headers.size(); i++) {
          if (StringUtils.equals(headers.get(i), priority) && validData(i)) {
            index = i;
            break;
          }
        }
      }
      if (index < 0) {
        return;
      }
      int finalIndex = index;
      values.sort((o1, o2) -> {
        Double d1;
        Double d2;
        if (o1.length <= finalIndex) {
          d1 = Double.MIN_VALUE;
        } else {
          d1 = Double.parseDouble(String.valueOf(o1[finalIndex]));
        }
        if (o2.length <= finalIndex) {
          d2 = Double.MIN_VALUE;
        } else {
          d2 = Double.parseDouble(String.valueOf(o2[finalIndex]));
        }

        if (StringUtils.equalsIgnoreCase(order, "asc")) {
          return d1.compareTo(d2);
        } else {
          return d2.compareTo(d1);
        }
      });
    }

    private boolean validData(int i) {
      for (Object[] value : values) {
        if (value.length <= i) {
          return false;
        }
        if (!(value[i] instanceof Number)) {
          return false;
        }
        Double d = ((Number) value[i]).doubleValue();
        if (!d.equals(0d)) {
          return true;
        }
      }
      return false;
    }
  }
}
