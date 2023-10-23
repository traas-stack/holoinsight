/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access;

import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import io.holoinsight.server.home.biz.service.TenantInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AccessRecordService {

  private static final ConcurrentHashMap<String, AtomicInteger> ACCESS_MAP =
      new ConcurrentHashMap<>();

  public static final String METRIC_METER_METRIC = "holoinsight_metering_api";

  @Autowired
  @Lazy
  private MetricStorage metricStorage;

  @Autowired
  private TenantInitService tenantInitService;

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
    ACCESS_MAP.computeIfAbsent(tsdbTenant + "_" + workspace, key -> new AtomicInteger(0)).addAndGet(1);
  }


  public void store() {
    Map<String, WriteMetricsParam> params = new HashMap<>();
    for (Map.Entry<String, AtomicInteger> entry : ACCESS_MAP.entrySet()) {
      int value = entry.getValue().getAndSet(0);
      long period = System.currentTimeMillis() / 1000 * 1000;
      String[] keys = StringUtils.split(entry.getKey(), "_");
      String tsdbTenant = keys[0];
      String workspace = keys[1];
      WriteMetricsParam writeMetricsParam = params.computeIfAbsent(tsdbTenant, k -> {
        WriteMetricsParam param = new WriteMetricsParam();
        param.setTenant(tsdbTenant);
        param.setPoints(new ArrayList<>());
        param.setFree(true);
        return param;
      });
      WriteMetricsParam.Point point = new WriteMetricsParam.Point();
      point.setMetricName(METRIC_METER_METRIC);
      Map<String, String> tags = new HashMap<>();
      tags.put("workspace", workspace);
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
