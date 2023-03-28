/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import static org.junit.platform.engine.TestDescriptor.Type.TEST;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import lombok.Getter;
import lombok.val;

/**
 * <p>
 * MemoryTestExecutionListener records all test execution result.
 * <p>
 * created at 2023/3/28
 *
 * @author xzchaoo
 */
public class MemoryTestExecutionListener implements TestExecutionListener {
  @Getter
  private Map<TestIdentifier, Record> records = new HashMap<>();

  @Override
  public synchronized void executionSkipped(TestIdentifier testIdentifier, String reason) {
    Record r = new Record();
    r.skipReason = reason;
    records.put(testIdentifier, r);
  }

  @Override
  public synchronized void executionStarted(TestIdentifier testIdentifier) {
    if (testIdentifier.getType() != TEST) {
      return;
    }
    Record r = new Record();
    r.startTime = System.currentTimeMillis();
    records.put(testIdentifier, r);
  }

  @Override
  public synchronized void executionFinished(TestIdentifier testIdentifier,
      TestExecutionResult testExecutionResult) {
    Record r = records.get(testIdentifier);
    if (r == null) {
      return;
    }
    r.endTime = System.currentTimeMillis();
    r.result = testExecutionResult;
  }

  public Map<String, Record> getDisplayRecords() {
    // Use TreeMap to sort keys
    Map<String, Record> map = new TreeMap<>();
    for (val e : this.records.entrySet()) {
      map.put(e.getKey().getUniqueId(), e.getValue());
    }
    return map;
  }

  public static class Record {
    public String skipReason;
    public TestExecutionResult result;
    private long startTime;
    private long endTime;

    public long costSeconds() {
      return (endTime - startTime) / 1000;
    }
  }

}
