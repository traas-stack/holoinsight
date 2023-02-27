/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute;

import io.holoinsight.server.home.facade.InspectConfig;
import lombok.Data;

/**
 * 计算所需信息
 *
 * @author wangsiyuan
 * @date 2022/10/13 7:48 下午
 */
@Data
public class ComputeInfo {

  private String traceId;

  private long period;

  private String tenant;


  public static ComputeInfo getComputeInfo(InspectConfig inspectConfig, long period) {
    ComputeInfo computeInfo = new ComputeInfo();
    computeInfo.setPeriod(period);
    computeInfo.setTraceId(inspectConfig.getTraceId());
    computeInfo.setTenant(inspectConfig.getTenant());
    return computeInfo;
  }

}
