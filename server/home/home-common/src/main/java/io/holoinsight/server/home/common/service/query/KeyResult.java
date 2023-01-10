/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/29 11:23 上午
 */
@Data
public class KeyResult {

  private String metric;
  private List<String> tags;
}
