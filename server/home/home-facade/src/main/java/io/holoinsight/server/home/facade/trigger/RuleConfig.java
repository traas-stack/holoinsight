/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

/**
 * @author masaimu
 * @version 2023-02-23 20:05:00
 */
public class RuleConfig {
  public Success success;

  public static RuleConfig defaultUpConfig() {
    RuleConfig config = new RuleConfig();
    config.success = new Success();
    config.success.customAmplitude = 0.8;
    config.success.customDuration = 2;
    config.success.customChangeRate = 0.2;
    return config;
  }

  public static RuleConfig defaultDownConfig() {
    RuleConfig config = new RuleConfig();
    config.success = new Success();
    config.success.customAmplitude = 0.05;
    config.success.customDuration = 2;
    config.success.customChangeRate = 0.5;
    return config;
  }

  public static class Success {
    public Double customAmplitude;
    public Integer customDuration;
    public Double customChangeRate;
  }
}
