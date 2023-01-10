/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.ob;

import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author zzhb101
 * @version 1.0: ObTask.java, v 0.1 2022年11月21日 上午11:59 jinsong.yjs Exp $
 */
@Data
@NoArgsConstructor
public class ObTask extends LocalIntegrationTask {

  public String network;
  public Integer port;
  public String dbName;
  public List<ObMetricConf> metrics;
}
