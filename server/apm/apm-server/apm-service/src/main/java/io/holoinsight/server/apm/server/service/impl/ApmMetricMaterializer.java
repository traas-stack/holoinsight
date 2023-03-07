/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.engine.MetricDefine;
import io.holoinsight.server.apm.engine.MetricsManager;
import io.holoinsight.server.apm.engine.storage.MetricStorage;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private MetricStorage apmMetricStorage;

  @Autowired
  private MetricsManager metricsManager;

  @Autowired
  private TenantOpsMapper tenantOpsMapper;

  @Autowired
  private SuperCacheService superCacheService;

  private static final long INTERVAL = 60000;

  private static final long DELAY = 5000;

  private static final String STEP = "1m";

  private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,
      new BasicThreadFactory.Builder().namingPattern("apm-materialize-scheduler-%d").build());

  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("apm-materialize-executor-%d").build());

  @PostConstruct
  public void start() {
    long now = System.currentTimeMillis();
    long nextPeriod = now / INTERVAL * INTERVAL + INTERVAL;
    long wait = nextPeriod - now + DELAY;
    SCHEDULER.scheduleAtFixedRate(this::materialize, wait, INTERVAL, TimeUnit.MILLISECONDS);
  }

  private void materialize() {
    long start = System.currentTimeMillis() / INTERVAL * INTERVAL - INTERVAL;
    long end = start + INTERVAL;
    Map<String, List<String>> indexGroups = new HashMap<>();
    MetaDataDictValue cachedGroups = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("apm_materialized_groups");
    if (cachedGroups != null) {
      indexGroups = GsonUtils.fromJson(cachedGroups.getDictValue(),
          new TypeToken<Map<String, List<String>>>() {}.getType());
    }
    QueryWrapper<TenantOps> wrapper = new QueryWrapper<>();
    List<TenantOps> tenantOpsList = tenantOpsMapper.selectList(wrapper);
    if (CollectionUtils.isNotEmpty(tenantOpsList)) {
      Map<String, List<String>> finalIndexGroups = indexGroups;
      tenantOpsList.forEach(tenantOps -> {
        String tenant = tenantOps.getTenant();
        Map<String, MetricDefine> metrics = metricsManager.getMetricDefines();
        if (metrics != null) {
          metrics.forEach((metricName, metricDefine) -> {
            List<String> groups = finalIndexGroups.get(metricDefine.getIndex());
            EXECUTOR.submit(() -> {
              try {
                log.info(
                    "[apm] ready to materialize metric, tenant={}, metric={}, start={}, end={}, step={}, groups={}",
                    tenant, metricName, start, end, STEP, groups);
                MetricValues metricValues = apmMetricStorage.queryMetric(tenant, metricName,
                    new Duration(start, end, STEP), null, groups);
                log.info(
                    "[apm] query metric success, tenant={}, metric={}, start={}, end={}, step={}, groups={}, series={}",
                    tenant, metricName, start, end, STEP, groups,
                    metricValues == null ? 0 : CollectionUtils.size(metricValues.getValues()));
                WriteMetricsParam param = new WriteMetricsParam();
                param.setTenant(tenant);
                param.setPoints(new ArrayList<>());
                AtomicInteger pointCnt = new AtomicInteger(0);
                if (metricValues != null && CollectionUtils.isNotEmpty(metricValues.getValues())) {
                  metricValues.getValues().forEach(value -> {
                    value.getValues().forEach((t, v) -> {
                      WriteMetricsParam.Point point = new WriteMetricsParam.Point();
                      point.setMetricName(metricName);
                      point.setTags(value.getTags());
                      point.setTimeStamp(t);
                      point.setValue(v);
                      param.getPoints().add(point);
                      pointCnt.incrementAndGet();
                    });
                  });
                }
                metricStorage.write(param).subscribe(unused -> {
                  log.info(
                      "[apm] materialize metric success, tenant={}, metric={}, start={}, end={}, step={}, points={}",
                      tenant, metricName, start, end, STEP, pointCnt.get());
                }, error -> {
                  log.error(
                      "[apm] materialize metric failed, tenant={}, metric={}, start={}, end={}, step={}, points={}",
                      tenant, metricName, start, end, STEP, pointCnt.get(), error);
                });
              } catch (Exception e) {
                log.error("[apm] materialize failed", e);
              }
            });
          });
        }
      });
    }
  }


}
