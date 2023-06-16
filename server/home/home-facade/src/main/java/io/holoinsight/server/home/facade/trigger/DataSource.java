/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/11 4:06 下午
 */
@Data
public class DataSource implements Serializable {

  private String metricType; // 监控类型

  private String product; // 产品

  private List<String> position;

  private String metric; // 监控项

  private List<String> groupBy; // 维度聚合

  private String downsample; // 时间窗口

  private String name; // 代称 （a、b）

  private List<Filter> filters; // 维度筛选

  private String fillType; // 数据补全逻辑

  private String aggregator; // 数据聚合函数
}
