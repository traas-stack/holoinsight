/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model.alicloud;

import com.alipay.cloudmonitor.registry.integration.model.CentralIntegrationTask;
import com.alipay.cloudmonitor.registry.integration.model.IntegrationTask;
import io.holoinsight.server.registry.model.integration.utils.DesensitizeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zzhb101
 * @version : AliCloudTask.java, v 0.1 2022年06月08日 14:03 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliCloudTask extends CentralIntegrationTask {
    private List<AlicloudConf> confs;

    private IntegrationTask desensitize() {
        if (this.confs != null) {
            this.confs.forEach(conf -> {
                conf.setAccessKeyId(DesensitizeUtil.around(conf.getAccessKeyId(), 3, 3));
                conf.setAccessKeySecret(DesensitizeUtil.around(conf.getAccessKeySecret(), 3, 3));
            });
        }
        return this;
    }
}