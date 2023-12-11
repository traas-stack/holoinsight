/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;
import org.springframework.util.CollectionUtils;

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
   * 指定存储表名称, 已经废弃，暂不删除
   */
  @Deprecated
  public String name;

  /**
   * 监控项名称，前端传入
   */
  public String tableName;

  /**
   * 真正的指标表名称，非前端传入，是后端自动生成
   */
  public String targetTable;

  // 关键字统计，数值提取，日志流量
  // contains/select/count
  public String metricType;
  public Boolean spm;

  // 关键字统计
  public String containValue;

  public List<String> tags;

  public List<Metric> metrics;

  /**
   * after filters
   */
  public List<AfterFilter> afterFilters;

  public Boolean logSample;
  public List<LogSampleRule> logSampleRules;

  // The maximum sample size of a single machine is 10, the default setting is 1
  public Integer sampleMaxCount = 1;

  // Logs that exceed 4096 are truncated
  public Integer sampleMaxLength = 4096;

  // pre calculate, 是否开启预计算
  public Boolean calculate;

  /**
   * agg 预聚合指标表名称，非前端传入，是后端自动生成
   */
  public String aggTableName;

  // 单机明细数据是否存储
  public Boolean notStorage;

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

  @Data
  public static class LogSampleRule implements Serializable {
    private static final long serialVersionUID = 6526193106427818978L;

    public String name;

    // "DIM" / "VALUE"
    public String keyType = "DIM";
    public FilterType filterType;
    // keyType = DIM
    public List<String> values;

    // keyType = VALUE
    public Double value;

  }

  public boolean checkLogPattern() {
    if (!CollectionUtils.isEmpty(metrics)) {
      for (Metric metric : metrics) {
        if (metric.getFunc().equals("loganalysis"))
          return true;
      }
    }
    return false;
  }

  public boolean checkLogSample() {
    return null != logSample && Boolean.TRUE == logSample;
  }
}
