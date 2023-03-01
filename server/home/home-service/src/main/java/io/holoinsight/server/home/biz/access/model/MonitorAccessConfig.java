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
 * @version 1.0: MonitorAccessConfig.java, v 0.1 2022-06-10 15:56 jinsong.yjs Exp $
 */
@Data
public class MonitorAccessConfig {

  /**
   *
   */
  private String accessId;

  private String accessKey;

  private String tenant;

  private Boolean online;

  /**
   * allow all metric access permission
   */
  private boolean accessAll = false;
  /**
   * allow metric list
   */
  private Set<String> accessRange = new HashSet<>();
  /**
   * server QPS， -1: no limit，0: in black list， other: normal flow limie policy
   */
  private long metricQps = -1L;
  private long metaQps = -1L;
  /**
   * flow limit configuration
   */
  private Map<String, Long> userRate = new HashMap<>();

  /**
   * metric query upper limit
   */
  private long dpsLimit = GatewayConstants.WEB_QUERY_DEFAULT_DPS_LIMIT;
  /**
   * tags query upper limit
   */
  private long tagsLimit = GatewayConstants.QUERY_DEFAULT_TAGS_LIMIT;

  /**
   * market plugin id
   */
  private Long marketPluginId;;

}
