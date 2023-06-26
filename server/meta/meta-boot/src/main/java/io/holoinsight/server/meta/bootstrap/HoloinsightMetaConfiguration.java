/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.bootstrap;

import io.holoinsight.server.common.springboot.ConditionalOnRole;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * created at 2022/11/24
 *
 * @author jsy1001de
 */
@ComponentScan({"io.holoinsight.server.meta"})
@EnableScheduling
@ConditionalOnRole("meta")
public class HoloinsightMetaConfiguration {
}
