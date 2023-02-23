/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.data;

import io.holoinsight.server.home.facade.trigger.DataSource;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/11 3:53 下午
 */
@Data
public class DataInput implements Serializable {

  private String query; // 表达式

  private List<DataSource> datasources; // 数据源

  private Long downsample; // 周期时间

  private String aggregator; // 周期聚合函数

}
