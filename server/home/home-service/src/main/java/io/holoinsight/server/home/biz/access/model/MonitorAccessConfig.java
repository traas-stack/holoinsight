/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorAccessConfig.java, v 0.1 2022年06月10日 3:56 下午 jinsong.yjs Exp $
 */
@Data
public class MonitorAccessConfig {

  /**
   *
   */
  private String accessId;

  private String accessKey;

  private String tenant;

  private String workspace;

  private Boolean online;

  /**
   * 是否允许访问所有指标
   */
  private boolean accessAll = false;
  /**
   * 允许访问的指标列表
   */
  private Set<String> accessRange = new HashSet<>();
  private Set<String> accessUrl = new HashSet<>();
  /**
   * 单机QPS， -1代表不限，0代表黑名单， 其他代表正常限流
   */
  private long metricQps = -1L;
  private long metaQps = -1L;
  /**
   * 用户限流信息
   */
  private Map<String, Long> userRate = new HashMap<>();

  /**
   * 一次查询指标中最多数据点
   */
  private long dpsLimit = GatewayConstants.WEB_QUERY_DEFAULT_DPS_LIMIT;
  /**
   * 一次查询维度中最多的Tags
   */
  private long tagsLimit = GatewayConstants.QUERY_DEFAULT_TAGS_LIMIT;

}
