/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.function;

import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;

import java.io.Serializable;

/**
 * @author wangsiyuan
 * @date 2022/3/17 2:44 下午
 */
public interface FunctionLogic extends Serializable {

  FunctionEnum getFunc();

  /**
   *
   * @param triggerDataResult
   * @param functionConfigParam
   * @return
   */
  TriggerResult invoke(TriggerDataResult triggerDataResult,
      FunctionConfigParam functionConfigParam);

}
