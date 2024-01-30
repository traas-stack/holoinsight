/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import java.util.HashMap;

/**
 * @author masaimu
 * @version 2023-02-23 20:05:00
 */
public class RuleConfig extends HashMap<String, RuleConfig.Success> {

  public static RuleConfig defaultUpConfig(String fieldName) {
    RuleConfig config = new RuleConfig();
    Success success = new Success();
    success.customAmplitude = 0.8;
    success.customDuration = 2;
    success.customChangeRate = 0.2;
    config.put(fieldName, success);
    return config;
  }

  public static RuleConfig defaultDownConfig(String fieldName) {
    RuleConfig config = new RuleConfig();
    Success success = new Success();
    success.customAmplitude = 0.05;
    success.customDuration = 2;
    success.customChangeRate = 0.5;
    config.put(fieldName, success);
    return config;
  }

  public static class Success {
    public Double customAmplitude;
    public Integer customDuration;
    public Double customChangeRate;
  }
}
