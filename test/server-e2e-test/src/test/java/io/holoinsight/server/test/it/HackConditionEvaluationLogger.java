/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.awaitility.core.ConditionEvaluationListener;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.EvaluatedCondition;
import org.awaitility.core.IgnoredException;
import org.awaitility.core.StartEvaluationEvent;
import org.awaitility.core.TimeoutEvent;

/**
 * <p>
 * This class is copy from {@link ConditionEvaluationLogger}, and removes some code liens to reduce
 * logs.
 * <p>
 * created at 2023/3/11
 *
 * @author xiangfeng.xzc
 */
class HackConditionEvaluationLogger implements ConditionEvaluationListener<Object> {
  private static final int MAX_IGNORE_COUNT = 12;
  private final Consumer<String> logPrinter;
  private final TimeUnit unit;
  private IgnoredException lastException;
  private int ignoreCount;

  /**
   * Uses {@link java.util.concurrent.TimeUnit#MILLISECONDS} as unit for elapsed and remaining time.
   */
  public HackConditionEvaluationLogger() {
    this(SECONDS);
  }

  /**
   * Specifies a consumer that is responsible for actually printing the logs
   *
   * @param logPrinter The logger to use
   */
  public HackConditionEvaluationLogger(Consumer<String> logPrinter) {
    this(logPrinter, MILLISECONDS);
  }

  /**
   * Specifies the {@link java.util.concurrent.TimeUnit} to use as unit for elapsed and remaining
   * time.
   *
   * @param unit The time unit to use.
   */
  public HackConditionEvaluationLogger(TimeUnit unit) {
    this(System.out::println, unit);
  }

  public HackConditionEvaluationLogger(Consumer<String> logPrinter, TimeUnit unit) {
    if (logPrinter == null) {
      throw new IllegalArgumentException("LogPrinter cannot be null");
    }
    if (unit == null) {
      throw new IllegalArgumentException("TimeUnit cannot be null");
    }
    this.logPrinter = logPrinter;
    this.unit = unit;
  }

  public void conditionEvaluated(EvaluatedCondition<Object> condition) {
    String description = condition.getDescription();
    long elapsedTime = unit.convert(condition.getElapsedTimeInMS(), MILLISECONDS);
    long remainingTime = unit.convert(condition.getRemainingTimeInMS(), MILLISECONDS);
    String unitAsString = unit.toString().toLowerCase();
    final String message;
    if (condition.isSatisfied()) {
      // message = String.format("%s after %d %s (remaining time %d %s, last poll interval was %s)",
      // description, elapsedTime, unitAsString, remainingTime, unitAsString,
      // new TemporalDuration(condition.getPollInterval()).toString());
    } else {
      message =
          String.format("%s (elapsed time %d %s, remaining time %d %s (last poll interval was %s))",
              description, elapsedTime, unitAsString, remainingTime, unitAsString,
              new TemporalDuration(condition.getPollInterval()).toString());
      logPrinter.accept(message);
    }
  }

  @Override
  public void beforeEvaluation(StartEvaluationEvent<Object> condition) {
    // logPrinter.accept(condition.getDescription());
  }

  @Override
  public void onTimeout(TimeoutEvent timeoutEvent) {
    logPrinter.accept(String.format("%s", timeoutEvent.getDescription()));
  }

  @Override
  public void exceptionIgnored(IgnoredException ignoredException) {
    ignoreCount++;
    if (ignoreCount > MAX_IGNORE_COUNT) {
      ignoreCount = 0;
      lastException = null;
    }

    if (lastException != null && lastException.getThrowable().getMessage()
        .equals(ignoredException.getThrowable().getMessage())) {
      return;
    }
    lastException = ignoredException;

    ignoredException.getThrowable().printStackTrace();
  }
}
