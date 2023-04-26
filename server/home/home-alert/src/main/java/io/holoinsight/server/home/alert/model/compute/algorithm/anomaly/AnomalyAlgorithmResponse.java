/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm.anomaly;

import lombok.Data;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2023/4/24 11:50 AM
 */
@Data
public class AnomalyAlgorithmResponse {
  /**
   * detectTime
   */
  private Long detectTime;
  /**
   * errorCode
   */
  private Map<String, Object> errorCode;
  /**
   * isException
   */
  private Boolean isException;
  /**
   * isSuccessful
   */
  private Boolean isSuccessful;
  /**
   * traceId
   */
  private Object traceId;
}
