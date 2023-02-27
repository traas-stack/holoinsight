/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute;

import io.holoinsight.server.home.facade.InspectConfig;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/3/16 6:44 下午
 */
@Data
public class ComputeContext {

  /**
   * 时间点
   */
  private long timestamp;

  // 告警配置
  private InspectConfig inspectConfig;


}
