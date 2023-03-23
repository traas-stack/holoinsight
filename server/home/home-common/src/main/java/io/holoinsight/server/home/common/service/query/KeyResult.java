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
 * @date 2022/4/29 11:23 上午
 */
@Data
public class KeyResult implements Measurable {

  private String metric;
  private List<String> tags;

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(tags)) {
      return 0;
    }
    return tags.size();
  }
}
