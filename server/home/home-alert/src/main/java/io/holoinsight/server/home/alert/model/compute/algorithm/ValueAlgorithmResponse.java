/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/10/13 9:11 下午
 */
@Data
public class ValueAlgorithmResponse {
  private String taskId;
  private String abnormalCategory;
  private AbnormalFeatures abnormalFeatures;
  private Integer anomalyDuration;
  private Object extInfo;
  private Boolean isException;
  private Object isRecovery;
  private String msg;
  private PassReasons passReasons;
  private String router;
}
