/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import io.holoinsight.server.common.service.Measurable;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/8/16 5:33 下午
 */
@Data
public class ValueResult implements Measurable {
  private String tag;
  private List<String> values;

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(values)) {
      return 0;
    }
    return values.size();
  }
}
