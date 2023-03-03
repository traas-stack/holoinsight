/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiwliu
 * @version : SearchTraceRequest.java, v 0.1 2022年09月19日 16:59 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryTraceRequest {
  /**
   * 租户
   */
  private String tenant;
  /**
   * 服务名称
   */
  private String serviceName;
  /**
   * 服务实例
   */
  private String serviceInstanceName;
  /**
   * trace_id 列表
   */
  private List<String> traceIds;
  /**
   * 服务端点
   */
  private String endpointName;
  /**
   * 查询区间
   */
  private Duration duration;
  /**
   * 最小持续时间
   */
  private int minTraceDuration;
  /**
   * 最大持续时间
   */
  private int maxTraceDuration;
  /**
   * trace 状态
   */
  private TraceState traceState;
  /**
   * 排序规则
   */
  private QueryOrder queryOrder;
  /**
   * 分页规则
   */
  private Pagination paging;
  /**
   * 查询条件
   */
  private List<Tag> tags;
}
