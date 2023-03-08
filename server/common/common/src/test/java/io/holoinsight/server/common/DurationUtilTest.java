/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DurationUtilTest {

  @Test
  public void testParse() {
    Map<String, Long> validFormats = new HashMap<>();
    validFormats.put("1s", 1000L);
    validFormats.put("5S", 5 * 1000L);
    validFormats.put("30second", 30 * 1000L);
    validFormats.put("1m", 60 * 1000L);
    validFormats.put("5MIN", 5 * 60 * 1000L);
    validFormats.put("10minute", 10 * 60 * 1000L);
    validFormats.put("1h", 60 * 60 * 1000L);
    validFormats.put("20HOUR", 20 * 60 * 60 * 1000L);
    validFormats.put("1d", 24 * 60 * 60 * 1000L);
    validFormats.put("3DAY", 3 * 24 * 60 * 60 * 1000L);
    validFormats.put("1week", 7 * 24 * 60 * 60 * 1000L);
    validFormats.put("3w", 3 * 7 * 24 * 60 * 60 * 1000L);
    validFormats.put("1M", 30 * 24 * 60 * 60 * 1000L);
    validFormats.put("3month", 3 * 30 * 24 * 60 * 60 * 1000L);
    validFormats.put("1year", 365 * 24 * 60 * 60 * 1000L);
    validFormats.put("10year", 10 * 365 * 24 * 60 * 60 * 1000L);
    validFormats.forEach((format, millis) -> {
      Long actual = DurationUtil.parse(format).toMillis();
      Assert.assertEquals(millis, actual);
    });
    Set<String> invalidFormats = new HashSet<>();
    invalidFormats.add("5");
    invalidFormats.add("d");
    invalidFormats.add("0s");
    invalidFormats.add("1mm");
    invalidFormats.add("1h1");
    invalidFormats.forEach(format -> {
      Assert.assertThrows(IllegalArgumentException.class, () -> DurationUtil.parse(format));
    });


  }

}
