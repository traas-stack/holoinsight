/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

class TemporalDuration implements TemporalAccessor {
  private static final Temporal BASE = LocalDateTime.of(0, 1, 1, 0, 0, 0, 0);

  private static final DateTimeFormatter dtf =
      new DateTimeFormatterBuilder().optionalStart().appendValue(YEAR).appendLiteral(" years ")
          .optionalEnd().optionalStart().appendLiteral(' ').appendValue(MONTH_OF_YEAR)
          .appendLiteral(" months ").optionalEnd().optionalStart().appendLiteral(' ')
          .appendValue(DAY_OF_MONTH).appendLiteral(" days ").optionalEnd().optionalStart()
          .appendLiteral(' ').appendValue(HOUR_OF_DAY).appendLiteral(" hours ").optionalEnd()
          .optionalStart().appendLiteral(' ').appendValue(MINUTE_OF_HOUR).appendLiteral(" minutes ")
          .optionalEnd().optionalStart().appendLiteral(' ').appendValue(SECOND_OF_MINUTE)
          .appendLiteral(" seconds").optionalEnd().optionalStart().appendLiteral(' ')
          .appendValue(MILLI_OF_SECOND).appendLiteral(" milliseconds").optionalEnd().toFormatter();


  private final Duration duration;
  private final Temporal temporal;

  TemporalDuration(Duration duration) {
    this.duration = duration;
    this.temporal = duration.addTo(BASE);
  }

  @Override
  public boolean isSupported(TemporalField field) {
    if (!temporal.isSupported(field)) {
      return false;
    }
    return temporal.getLong(field) - BASE.getLong(field) != 0L;
  }

  @Override
  public long getLong(TemporalField temporalField) {
    if (!isSupported(temporalField)) {
      throw new UnsupportedTemporalTypeException(temporalField.toString());
    }
    return temporal.getLong(temporalField) - BASE.getLong(temporalField);
  }

  public Duration getDuration() {
    return duration;
  }

  @Override
  public String toString() {
    if (duration.compareTo(Duration.ofMillis(1)) < 0) {
      return duration.toNanos() + " nanoseconds";
    } else {
      return dtf.format(this).trim();
    }
  }

  static String formatAsString(Duration duration) {
    return new TemporalDuration(duration).toString();
  }
}
