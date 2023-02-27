/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.trigger.Trigger;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/10/11 6:07 下午
 */
public interface AlarmLoadData {

  List<DataResult> queryDataResult(ComputeTaskPackage computeTask, InspectConfig inspectConfig,
      Trigger trigger);
}
