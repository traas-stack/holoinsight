/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;

import io.holoinsight.server.agg.v1.core.conf.Window;

/**
 * <p>
 * created at 2024/1/29
 *
 * @author xzchaoo
 */
public class UtilsTest {

  @Test
  public void test_align() {
    long now = System.currentTimeMillis();
    Window w = new Window();
    w.setInterval(86400000);
    long aligned = Utils.align(now, w);
    long aligned2 = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault())
        .truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
    Assert.assertEquals(aligned2, aligned);
    // System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(aligned)));
  }
}
