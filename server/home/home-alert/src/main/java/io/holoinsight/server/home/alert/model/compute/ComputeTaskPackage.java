/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute;

import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.InspectConfig;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/28 8:00 下午
 */
@Data
public class ComputeTaskPackage {

  private String type = "alarmRule"; // 不同的计算流程（spark、pql）

  /**
   * traceId
   */
  private String traceId;

  /**
   * 检测时间点
   */
  private long timestamp;

  /**
   * 告警配置
   */
  public List<InspectConfig> inspectConfigs;

  // 后续可以考虑聚合数据源查询

  /**
   * 告警通知记录
   */
  public AlertNotifyRecordDTO alertNotifyRecord;

}
