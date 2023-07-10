/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginConf.java, v 0.1 2022年03月14日 8:13 下午 jinsong.yjs Exp $
 */
@Data
public class CustomPluginConf implements Serializable {
  private static final long serialVersionUID = 7595849765231492514L;

  public ExtraConfig extraConfig;
  /**
   * 日志路径
   */
  public List<LogPath> logPaths;

  /**
   * 前置过滤黑名单
   */
  public List<Filter> blackFilters;

  /**
   * 前置过滤白名单
   */
  public List<Filter> whiteFilters;

  /**
   * 日志切分规则 分隔符切分/左起右至/正则表达式
   */
  public LogParse logParse = new LogParse();

  /**
   * 采集范围
   */
  public CloudMonitorRange collectRanges;

  /**
   * 全局维度定义
   */
  public List<SplitCol> splitCols;

  public Boolean spm;

  public SpmCols spmCols;

  /**
   * 监控指标定义
   */
  public List<CollectMetric> collectMetrics;

  @Data
  public static class SplitCol implements Serializable {

    private static final long serialVersionUID = -8509759191868776391L;

    public String name;

    public Rule rule;

    public String colType;
  }

  @Data
  public static class ExtraConfig {
    public Integer keyCleanInterval = 0;

    public Integer maxKeySize = 20000;

    public String charset = "utf-8";

    public Integer agentLimitKB = -1;
  }

  @Data
  public static class SpmCols {
    public String countKey;
    public String costKey;
    public String resultKey;
    public List<String> successValue;
  }
}
