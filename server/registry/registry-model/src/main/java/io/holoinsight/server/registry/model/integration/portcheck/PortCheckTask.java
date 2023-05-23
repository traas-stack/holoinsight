/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.portcheck;

import io.holoinsight.server.registry.model.integration.IntegrationTask;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author zzhb101
 * @version 1.0: PortCheckTask.java, v 0.1 2022年11月21日 上午11:56 jinsong.yjs Exp $
 */
@Data
@NoArgsConstructor
public class PortCheckTask extends IntegrationTask {
  // [必填] 探测的端口
  public Integer port;

  public List<Integer> ports;

  // [可选] 默认是tcp
  public String network;

  // [可选] 超时时间 单位是ms 默认是3000
  public Long timeout;

  // [可选] 连续执行几次探测
  public Integer times;

  // [可选] 网络模式, 默认是AGENT
  public String networkMode;

}
