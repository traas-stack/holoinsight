/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author jinyan.ljw
 * @date 2023/1/10
 */
@Configuration
@ConditionalOnProperty(value = "holoinsight.metric.storage.type", havingValue = "ceresdbx")
public class HoloinsightCeresdbxConfiguration {

}
