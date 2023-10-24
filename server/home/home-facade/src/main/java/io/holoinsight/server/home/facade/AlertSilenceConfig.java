/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author masaimu
 * @version 2023-10-19 20:41:00
 */
@Data
public class AlertSilenceConfig {

  // default, gradual， fixed
  private String silenceMode = "default";
  private int silenceDuringMin = 10;

  private int step = 0;

  private long silencePeriod;

  public boolean timeIsUp(Long alarmTime) {
    if (StringUtils.equals(silenceMode, "default")) {
      return true;
    }
    if (alarmTime == null) {
      return false;
    }
    long interval = alarmTime - silencePeriod;
    if (StringUtils.equals(silenceMode, "fixed") && interval >= silenceDuringMin * 60000L) {
      return true;
    }
    if (StringUtils.equals(silenceMode, "gradual") && interval >= getGradualDuringMin() * 60000L) {
      return true;
    }
    return false;
  }

  private long getGradualDuringMin() {
    switch (step) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return 3;
      case 3:
        return 5;
      default:
        return 10;
    }
  }

  public void updatePeriod(Long alarmTime) {
    silencePeriod = alarmTime;
    if (StringUtils.equals(silenceMode, "gradual")) {
      step++;
    }
  }

  public boolean needShoot(Long alarmTime) {
    return alarmTime != null && alarmTime == silencePeriod;
  }
}
