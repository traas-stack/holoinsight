/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.home.facade.emuns.PeriodType;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author masaimu
 * @version 2023-10-23 18:02:00
 */
public class AlertSilenceConfigTest {

  @Test
  public void fixed() {
    AlertSilenceConfig alertSilenceConfig = new AlertSilenceConfig();
    alertSilenceConfig.setSilenceMode("fixed");

    long cur = PeriodType.MINUTE.rounding(System.currentTimeMillis());
    for (int i = 0; i < 50; i++) {
      if (alertSilenceConfig.timeIsUp(cur)) {
        alertSilenceConfig.updatePeriod(cur);
      }

      if (alertSilenceConfig.needShoot(cur)) {
        System.out.println(DateUtil.getDateOf_YYMMDD_HHMMSS(new Date(cur)) + " "
            + alertSilenceConfig.needShoot(cur));
      }

      cur += PeriodType.MINUTE.intervalMillis();
    }
  }

  @Test
  public void defaulted() {
    AlertSilenceConfig alertSilenceConfig = new AlertSilenceConfig();

    long cur = PeriodType.MINUTE.rounding(System.currentTimeMillis());
    for (int i = 0; i < 50; i++) {
      if (alertSilenceConfig.timeIsUp(cur)) {
        alertSilenceConfig.updatePeriod(cur);
      }

      if (alertSilenceConfig.needShoot(cur)) {
        System.out.println(DateUtil.getDateOf_YYMMDD_HHMMSS(new Date(cur)) + " "
            + alertSilenceConfig.needShoot(cur));
      }

      cur += PeriodType.MINUTE.intervalMillis();
    }
  }

  @Test
  public void gradual() {
    AlertSilenceConfig alertSilenceConfig = new AlertSilenceConfig();
    alertSilenceConfig.setSilenceMode("gradual");

    long cur = PeriodType.MINUTE.rounding(System.currentTimeMillis());
    for (int i = 0; i < 50; i++) {
      if (alertSilenceConfig.timeIsUp(cur)) {
        alertSilenceConfig.updatePeriod(cur);
      }

      if (alertSilenceConfig.needShoot(cur)) {
        System.out.println(DateUtil.getDateOf_YYMMDD_HHMMSS(new Date(cur)) + " "
            + alertSilenceConfig.needShoot(cur));
      }

      cur += PeriodType.MINUTE.intervalMillis();
    }
  }
}
