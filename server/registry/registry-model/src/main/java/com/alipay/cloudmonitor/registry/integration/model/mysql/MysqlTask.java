/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model.mysql;

import com.alipay.cloudmonitor.registry.integration.model.CentralIntegrationTask;
import com.alipay.cloudmonitor.registry.integration.model.IntegrationTask;
import io.holoinsight.server.registry.model.integration.utils.DesensitizeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zzhb101
 * @version : MysqlTask.java, v 0.1 2022年06月08日 14:03 wanpeng.xwp Exp $
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
