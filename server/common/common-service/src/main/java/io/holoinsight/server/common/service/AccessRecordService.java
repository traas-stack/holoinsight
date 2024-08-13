/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AccessRecordService {

  protected static final ConcurrentHashMap<Map<String, String>, AtomicInteger> ACCESS_MAP =
      new ConcurrentHashMap<>();

  public static final String METRIC_METER_METRIC = "holoinsight_metering_api";

  @Autowired
  @Lazy
  private MetricStorage metricStorage;

  @PostConstruct
  public void init() {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
      try {
        store();
      } catch (Exception e) {
        log.error("store openapi access record fail", e);
      }
    }, 0, 1, TimeUnit.MINUTES);
  }

  public void record(String tsdbTenant, String workspace) {
    Map<String, String> tags = new HashMap<>();
    tags.put("tenant", tsdbTenant);
    tags.put("workspace", workspace);
    ACCESS_MAP.computeIfAbsent(tags, key -> new AtomicInteger(0)).addAndGet(1);
  }


  public void store() {
    Map<String, WriteMetricsParam> params = new HashMap<>();
    for (Map.Entry<Map<String, String>, AtomicInteger> entry : ACCESS_MAP.entrySet()) {
      int value = entry.getValue().getAndSet(0);
      long period = System.currentTimeMillis() / 1000 * 1000;
      Map<String, String> tags = entry.getKey();
      String tsdbTenant = tags.get("tenant");
      WriteMetricsParam writeMetricsParam = params.computeIfAbsent(tsdbTenant, k -> {
        WriteMetricsParam param = new WriteMetricsParam();
        param.setTenant(tsdbTenant);
        param.setPoints(new ArrayList<>());
        param.setFree(true);
        return param;
      });
      WriteMetricsParam.Point point = new WriteMetricsParam.Point();
      point.setMetricName(METRIC_METER_METRIC);
      point.setTags(tags);
      point.setTimeStamp(period);
      point.setValue(value);
      writeMetricsParam.getPoints().add(point);
    }
    for (WriteMetricsParam p : params.values()) {
      log.info("write openapi metering, tenant={}, points={}", p.getTenant(), p.getPoints().size());
      metricStorage.write(p).subscribe(null, error -> log.error("output metric meter fail", error),
          null);
    }
  }
}
