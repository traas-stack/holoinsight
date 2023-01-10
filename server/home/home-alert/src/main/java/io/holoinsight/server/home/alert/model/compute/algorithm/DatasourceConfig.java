/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import lombok.Data;

import java.util.List;

/**
 * 数据源配置
 *
 * @author wangsiyuan
 * @date 2022/10/28 2:58 PM
 */
@Data
public class DatasourceConfig {

  /**
   * 字段
   */
  private List<String> fields;

  /**
   * 源
   */
  private String source;
}
