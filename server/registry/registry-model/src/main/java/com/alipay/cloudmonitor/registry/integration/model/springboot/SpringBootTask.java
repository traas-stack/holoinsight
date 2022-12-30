/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model.springboot;

import com.alipay.cloudmonitor.registry.integration.model.LocalIntegrationTask;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzhb101
 * @version : JvmTask.java, v 0.1 2022年06月29日 14:03 wanpeng.xwp Exp $
 */
@Data
@NoArgsConstructor
public class SpringBootTask extends LocalIntegrationTask {
    private SpringBootConf conf;
}