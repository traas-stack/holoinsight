/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitor background tasks such as alarm calculation, and fuse when the threshold is reached. Used
 * for health check, e2e test.
 *
 * @author masaimu
 * @version 2023-03-13 18:27:00
 */
public class FuseProtector {

  private static final String CRITICAL = "critical";
  private static final String NORMAL = "normal";

  public static final String CRITICAL_AlertSaveHistoryHandler = "AlertSaveHistoryHandler";
  public static final String CRITICAL_AlertTaskCompute = "AlertTaskCompute";
  public static final String CRITICAL_AlertTaskScheduler = "AlertTaskScheduler";
  public static final String CRITICAL_GetSubscription = "GetSubscription";

  public static final String NORMAL_NotifyChain = "NotifyChain";
  public static final String NORMAL_AlertNotifyHandler = "AlertNotifyHandler";
  public static final String NORMAL_AlertSaveHistoryDetail = "AlertSaveHistoryDetail";
  public static final String NORMAL_MakeAlertRecover = "MakeAlertRecover";
  public static final String NORMAL_GetSubscriptionDetail = "GetSubscriptionDetail";

  protected static Map<String /* name */, List<EventResult>> criticalEventMap =
      new ConcurrentHashMap<>();
  protected static Map<String /* name */, List<EventResult>> normalEventMap =
      new ConcurrentHashMap<>();
  protected static Map<String /* name */, AtomicInteger> completeEventMap =
      new ConcurrentHashMap<>();

  public static void voteCriticalError(String name, String msg) {
    List<EventResult> set =
        criticalEventMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>());
    set.add(new EventResult(CRITICAL, name, msg));
  }

  public static void voteNormalError(String name, String msg) {
    List<EventResult> set = normalEventMap.computeIfAbsent(name, k -> new CopyOnWriteArrayList<>());
    set.add(new EventResult(NORMAL, name, msg));
  }

  public static void voteComplete(String name) {
    AtomicInteger atomicInteger = completeEventMap.computeIfAbsent(name, k -> new AtomicInteger(0));
    atomicInteger.incrementAndGet();
  }

  /**
   *
   * @throws HoloInsightCriticalException failed assert
   */
  public static synchronized void doAssert() throws HoloInsightCriticalException {
    if (!CollectionUtils.isEmpty(criticalEventMap)) {
      EventResult eventResult = getFirstEventResult(criticalEventMap, 0);
      if (eventResult != null) {
        throw new HoloInsightCriticalException(eventResult.msg, eventResult.name,
            eventResult.timestamp);
      }
    }

    if (CollectionUtils.isEmpty(normalEventMap)) {
      return;
    }

    if (CollectionUtils.isEmpty(completeEventMap)) {
      EventResult eventResult = getFirstEventResult(normalEventMap, 10);
      if (eventResult != null) {
        throw new HoloInsightCriticalException(eventResult.msg, eventResult.name,
            eventResult.timestamp);
      }
    }

    compareSize(completeEventMap, normalEventMap, 10);
  }

  /**
   *
   * @return true: pass, false: pending
   */
  public static synchronized boolean doAssert(List<String> keywords) {
    if (!CollectionUtils.isEmpty(keywords)) {
      for (String keyword : keywords) {
        AtomicInteger atomicInteger = completeEventMap.get(keyword);
        if (atomicInteger == null || atomicInteger.get() <= 0) {
          return false;
        }
      }
    }
    return true;
  }

  public static synchronized void clean() {
    criticalEventMap = new ConcurrentHashMap<>();
    normalEventMap = new ConcurrentHashMap<>();
    completeEventMap = new ConcurrentHashMap<>();
  }

  private static void compareSize(Map<String, AtomicInteger> completeEventMap,
      Map<String, List<EventResult>> normalEventMap, int threshold) {
    Set<String> keySet = new HashSet<>();
    keySet.addAll(completeEventMap.keySet());
    keySet.addAll(normalEventMap.keySet());
    for (String key : keySet) {
      AtomicInteger completeList = completeEventMap.get(key);
      int completeSize = completeList == null ? 0 : completeList.get();
      List<EventResult> normalList = normalEventMap.get(key);
      int normalSize = CollectionUtils.isEmpty(normalList) ? 0 : normalList.size();
      if (completeSize < normalSize && normalSize > threshold) {
        throw new HoloInsightCriticalException("more fail result", key);
      }
    }
  }

  private static EventResult getFirstEventResult(Map<String, List<EventResult>> eventMap,
      int threshold) {
    for (Map.Entry<String /* name */, List<EventResult>> entry : eventMap.entrySet()) {
      List<EventResult> eventResults = entry.getValue();
      if (!CollectionUtils.isEmpty(eventResults) && eventResults.size() > threshold) {
        return eventResults.get(0);
      }
    }
    return null;
  }

  public static class EventResult {
    String level;
    String name;
    String msg;
    long timestamp;

    public EventResult(String level, String name, String msg) {
      this.level = level;
      this.name = name;
      this.msg = msg;
      this.timestamp = System.currentTimeMillis();
    }
  }
}
