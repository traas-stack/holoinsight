/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: SepSplitConf.java, v 0.1 2022年03月14日 8:25 下午 jinsong.yjs Exp $
 */
@Data
public class CollectMetric implements Serializable {
  private static final long serialVersionUID = -616464385087632888L;

  /**
   * 指定存储表名称
   */
  public String name;

  /**
   * 监控项名称
   */
  public String tableName;

  /**
   * 真正的指标表名称
   */
  public String targetTable;

  // 关键字统计，数值提取，日志流量
  // contains/select/count
  public String metricType;

  // 关键字统计
  public String containValue;

  public List<String> tags;

  public List<Metric> metrics;

  /**
   * after filters
   */
  public List<AfterFilter> afterFilters;

  @Data
  public static class Metric implements Serializable {

    private static final long serialVersionUID = 6526193106427818977L;

    public String name;
    public String displayName;
    /**
     * sum/avg/max/min/count
     */
    public String func;

  }

  @Data
  public static class AfterFilter implements Serializable {
    private static final long serialVersionUID = 6526193106427818978L;

    public String name;
    public FilterType filterType;

    public List<String> values;
  }
}
