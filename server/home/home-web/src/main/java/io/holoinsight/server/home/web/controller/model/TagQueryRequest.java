/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/8/16 7:00 下午
 */
@Data
public class TagQueryRequest {

  private String metric;

  private String key;

  private Map<String, String> conditions;
}
