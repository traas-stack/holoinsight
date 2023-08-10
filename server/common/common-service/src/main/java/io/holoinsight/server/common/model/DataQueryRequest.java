/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DataQueryRequest.java, v 0.1 2022年05月17日 10:47 上午 jinsong.yjs Exp $
 */
@Data
public class DataQueryRequest {

  // 租户
  public String tenant;

  // 联合查询，例如 (a+b)*c，a、b、c为datasource的name引用
  public String query;

  // 查询数据源
  public List<QueryDataSource> datasources;

  public String fillPolicy;

  public String downsample;

  @Data
  public static class QueryDataSource {

    // 数据源名称，配合外层联合查询使用
    public String name;
    // 查询开始时间
    public Long start;
    // 查询截止时间
    public Long end;
    // 查询指标
    public String metric;
    // 聚合方式
    public String aggregator;
    // 填充策略
    public String fillPolicy;
    // 过滤条件
    public List<QueryFilter> filters;
    // 滑动窗口查询
    public SlidingWindow slidingWindow;
    // 降精度查询周期，例如1m、5m，降精度聚合方式为aggregator
    public String downsample;
    // 聚合分组
    public List<String> groupBy;
    // whether the APM metrics is obtained by materializing them to the metric storage in advance
    // instead of post-calculation
    public boolean apmMaterialized;
    public String ql;

    public Map<String /* column name */, String /* expression */> select;
  }

  @Data
  public static class QueryFilter {
    // 过滤类型，参考com.alibaba.hitsdb.client.value.type.FilterType
    public String type;
    // 过滤tagk
    public String name;
    // 过滤tagv
    public String value;
  }

  @Data
  public static class SlidingWindow {
    // 窗口长度
    public Long windowMs;
    // 聚合方式
    public String aggregator;
  }
}
