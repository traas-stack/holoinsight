/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.springboot;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * created at 2022/11/30
 *
 * @author xzchaoo
 */
@Data
@ConfigurationProperties(prefix = "holoinsight")
public class HoloinsightProperties {
  private Roles roles = new Roles();
  private Features features = new Features();

  @Data
  public static class Roles {
    private Set<String> active = new HashSet<>();
  }

  @Data
  public static class Features {
    private Set<String> active = new HashSet<>();
  }
}
