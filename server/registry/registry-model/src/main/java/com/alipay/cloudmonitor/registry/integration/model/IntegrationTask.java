/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model;

import io.holoinsight.server.registry.model.integration.GaeaTask;
import io.holoinsight.server.registry.model.ExecuteRule;
import lombok.Data;

/**
 * @author zzhb101
 * @version : IntegrationTask.java, v 0.1 2022年06月08日 14:01 wanpeng.xwp Exp $
 */
@Data
public abstract class IntegrationTask extends GaeaTask {
  private String name;
  private String type;
  private ExecuteRule executeRule;
}
