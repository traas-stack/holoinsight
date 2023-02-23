/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/3/21 11:11 上午
 */
@Data
public class TriggerAIResult extends TriggerResult {

  private Double anomalyDuration;

  private Double baseLine;

  private Double changeRate;

  private String msg;

}
