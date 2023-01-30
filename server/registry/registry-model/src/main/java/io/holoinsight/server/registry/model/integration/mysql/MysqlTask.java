/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.mysql;

import io.holoinsight.server.registry.model.integration.CentralIntegrationTask;
import io.holoinsight.server.registry.model.integration.IntegrationTask;
import io.holoinsight.server.registry.model.integration.utils.DesensitizeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zzhb101
 * @version : MysqlTask.java, v 0.1 2022年06月08日 14:03 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlTask extends CentralIntegrationTask {
  private List<MysqlConf> confs;

  private IntegrationTask desensitize() {
    if (this.confs != null) {
      this.confs.forEach(conf -> {
        conf.setPassword(DesensitizeUtil.around(conf.getPassword(), 3, 3));
      });
    }
    return this;
  }
}
