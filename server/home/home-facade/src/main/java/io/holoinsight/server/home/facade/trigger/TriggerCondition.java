/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/11 3:29 下午
 */
@Data
public class TriggerCondition {

  private String stepTime;

  private int stepNum;

  private String triggerStepNum;

  List<CompareParam> compareParam;

}
