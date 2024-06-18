/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto;

import io.holoinsight.server.common.dao.emuns.CustomPluginPeriodType;
import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: IntegrationPlugin.java, v 0.1 2022年11月17日 上午10:52 jinsong.yjs Exp $
 */
@Data
public class IntegrationConfig {
  public String name;

  public CustomPluginPeriodType periodType;

  public Object conf;
}
