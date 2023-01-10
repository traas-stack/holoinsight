/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * created at 2022/11/18
 *
 * @author xzchaoo
 */
@ConfigurationProperties(prefix = "holoinsight.security")
@Getter
@Setter
public class HoloinsightSecurityProperties {
  /**
   * Http requests with 'host' header in this 'whiteHosts' are allowed without any auth checks.
   */
  private Set<String> whiteHosts = new HashSet<>();
}
