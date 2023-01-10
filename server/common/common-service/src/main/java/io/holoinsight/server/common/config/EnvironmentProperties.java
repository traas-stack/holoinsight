/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author likun (saimu.msm@antfin.com)
 * @version 2023-01-06 14:55:00
 */
@ConfigurationProperties(prefix = "holoinsight.env")
@Data
public class EnvironmentProperties {
    private String deploymentSite;
}
