/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.engine.postcal.MetricsManager;
import io.holoinsight.server.apm.engine.storage.MetricStorage;
import io.holoinsight.server.apm.server.service.MetricService;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ApmMetricMaterializer is used to periodically query aggregated metrics from text storage engines
 * such as elasticsearch, and then write them to time-series metrics storage engines such as CeresDB
 * to optimize query performance in some high-frequency request scenarios.
 */

@Component
@Slf4j
public class ApmMetricMaterializer {

  @Autowired
  protected io.holoinsight.server.extension.MetricStorage metricStorage;


  @Autowired
  private MetricService apmMetricService;

  @Autowired
  private MetricsManager metricsManager;

  @Autowired
  private TenantOpsMapper tenantOpsMapper;

  private static final long INTERVAL = 60000;

  private static final long DELAY = 30000;

  private static final String STEP = "1m";

  // The number of cycles to fix forward each time it is materialized, this is to deal with the data
  // inaccuracy problem caused by trace recording delay.
  // Notice that the repaired data will be repeatedly written to the MetricStore, so it is necessary
  // to ensure that the MetricStore's policy for repeated data is OVERWRITE instead of APPEND.
  private static final int REPAIR_PERIODS = 0;

  private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,
      new BasicThreadFactory.Builder().namingPattern("apm-materialize-scheduler-%d").build());

  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(4096),
          new BasicThreadFactory.Builder().namingPattern("apm-materialize-executor-%d").build());

  @PostConstruct
  public void start() {
    long now = System.currentTimeMillis();
    long nextPeriod = now / INTERVAL * INTERVAL + INTERVAL;
    long wait = nextPeriod - now + DELAY;
    SCHEDULER.scheduleAtFixedRate(this::materialize, wait, INTERVAL, TimeUnit.MILLISECONDS);
  }

  private void materialize() {
    long now = System.currentTimeMillis();
    long start = now / INTERVAL * INTERVAL - INTERVAL * (REPAIR_PERIODS + 1);
    long end = now / INTERVAL * INTERVAL;
    List<String> metrics = metricsManager.listMaterializedMetrics();
    QueryWrapper<TenantOps> wrapper = new QueryWrapper<>();
    List<TenantOps> tenantOpsList = tenantOpsMapper.selectList(wrapper);
    if (CollectionUtils.isNotEmpty(tenantOpsList)) {
      tenantOpsList.forEach(tenantOps -> {
        String tenant = tenantOps.getTenant();
        metrics.forEach(metric -> {
          EXECUTOR.submit(() -> {
            try {
              log.info(
                  "[apm] ready to materialize metric, tenant={}, metric={}, start={}, end={}, step={}",
                  tenant, metric, start, end, STEP);
              MetricValues metricValues = apmMetricService.queryMetric(tenant, metric,
                  new Duration(start, end, STEP), null);
              log.info(
                  "[apm] query metric success, tenant={}, metric={}, start={}, end={}, step={}, series={}",
                  tenant, metric, start, end, STEP,
                  metricValues == null ? 0 : CollectionUtils.size(metricValues.getValues()));
              WriteMetricsParam param = new WriteMetricsParam();
              param.setTenant(tenant);
              param.setPoints(new ArrayList<>());
              AtomicInteger pointCnt = new AtomicInteger(0);
              if (metricValues != null && CollectionUtils.isNotEmpty(metricValues.getValues())) {
                metricValues.getValues().forEach(value -> {
                  value.getValues().forEach((t, v) -> {
                    WriteMetricsParam.Point point = new WriteMetricsParam.Point();
                    point.setMetricName(metric);
                    if (value.getTags() != null) {
                      value.getTags().put("tenant", tenant);
                    }
                    point.setTags(value.getTags());
                    point.setTimeStamp(t);
                    point.setValue(v);
                    param.getPoints().add(point);
                    pointCnt.incrementAndGet();
                  });
                });
              }
              metricStorage.write(param).block();
              log.info(
                  "[apm] materialize metric success, tenant={}, metric={}, start={}, end={}, step={}, points={}",
                  tenant, metric, start, end, STEP, pointCnt.get());
            } catch (Exception e) {
              log.error(
                  "[apm] materialize metric failed, tenant={}, metric={}, start={}, end={}, step={}",
                  tenant, metric, start, end, STEP, e);
            }
          });
        });
      });
    }
  }
}
