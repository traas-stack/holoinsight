/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.plugin.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author jsy1001de
 * @version 1.0: LogPluginConfig.java, v 0.1 2022年11月21日 下午4:38 jinsong.yjs Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogPluginConfig extends BasePluginConfig{
    public String       name;

}