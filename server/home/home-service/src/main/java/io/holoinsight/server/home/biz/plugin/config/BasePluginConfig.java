/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.config;

import lombok.Data;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: BasePluginConfig.java, v 0.1 2022年11月21日 下午4:47 jinsong.yjs Exp $
 */
@Data
public class BasePluginConfig {
  public List<String> range;

  public MetaLabel metaLabel;
}
