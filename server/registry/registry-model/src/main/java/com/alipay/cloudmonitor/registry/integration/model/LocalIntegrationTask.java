/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package com.alipay.cloudmonitor.registry.integration.model;

import io.holoinsight.server.registry.model.Elect;
import lombok.Data;

import java.util.Map;

/**
 * @author zzhb101
 * @version : IntegrationTask.java, v 0.1 2022年06月08日 14:01 wanpeng.xwp Exp $
 */
@Data
public abstract class LocalIntegrationTask extends IntegrationTask {
    private Map<String, Elect.RefMeta> refMetas;
}