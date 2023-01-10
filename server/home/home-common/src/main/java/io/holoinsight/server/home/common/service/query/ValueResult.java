/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/8/16 5:33 下午
 */
@Data
public class ValueResult {
  private String tag;
  private List<String> values;
}
