/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author sw1136562366
 * @version : TraceAuthEncryptConfiguration.java, v 0.1 2023年08月08日 16:26 sw1136562366 Exp $
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "trace-auth-encrypt")
public class TraceAuthEncryptConfiguration {
  private boolean enable;
  private String secretKey;
  private String iv;
}
