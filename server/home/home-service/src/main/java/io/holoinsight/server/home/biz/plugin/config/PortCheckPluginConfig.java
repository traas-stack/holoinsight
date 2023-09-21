/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: PortCheckPluginConfig.java, v 0.1 2022年11月21日 下午4:39 jinsong.yjs Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PortCheckPluginConfig extends BasePluginConfig {
  public Integer port;
  public List<Integer> ports;
  public String networkMode;
}
