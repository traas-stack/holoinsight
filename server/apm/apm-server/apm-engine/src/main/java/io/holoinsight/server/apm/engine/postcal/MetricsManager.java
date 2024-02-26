/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.postcal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.core.ApmConfig;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MetricsManager {

  @Value("classpath:metrics/metrics.json")
  private Resource resource;

  @Autowired
  private SuperCacheService superCacheService;

  @Autowired
  private ApmConfig apmConfig;

  @Getter
  private volatile Map<String, MetricDefine> metricDefines = new HashMap<>();

  private Map<String, MetricDefine> localMetricDefines = new HashMap<>();

  @PostConstruct
  public void init() throws IOException {
    String metricDefineJson;
    try (InputStream is = resource.getInputStream()) {
      metricDefineJson = IOUtils.toString(is, Charset.defaultCharset());
    }
    List<MetricDefine> materializedMetrics = GsonUtils.get().fromJson(metricDefineJson,
        new TypeToken<List<MetricDefine>>() {}.getType());
    log.info("[apm] load metric definitions from resource: {}", materializedMetrics);
    for (MetricDefine md : materializedMetrics) {
      localMetricDefines.put(md.getName(), md);
    }
    Map<String, MetricDefine> metricDefines = new HashMap<>(localMetricDefines);
    updateMetricDefines(metricDefines);
  }

  private void updateMetricDefines(Map<String, MetricDefine> metricDefines) {
    Set<String> blacklist = apmConfig.getMaterialize().getBlacklist();
    if (!blacklist.isEmpty()) {
      metricDefines.keySet().removeIf(metric -> blacklist.stream().anyMatch(metric::startsWith));
    }
    this.metricDefines = metricDefines;
  }

  /**
   * Metrics defined in the database have higher priority, which can override the content in
   * metrics.json
   */
  @Scheduled(initialDelay = 10000L, fixedDelay = 60000L)
  private void loadFromDB() {
    Map<String, MetricDefine> metricDefines = new HashMap<>(localMetricDefines);
    try {
      MetaDataDictValue metricDefineDictVal = superCacheService.getSc().metaDataDictValueMap
          .getOrDefault("global_config", new HashMap<>()).get("apm_materialized_metrics");
      if (metricDefineDictVal != null) {
        List<MetricDefine> materializedMetrics = GsonUtils.get().fromJson(
            metricDefineDictVal.getDictValue(), new TypeToken<List<MetricDefine>>() {}.getType());
        log.info("[apm] load metric definitions from database: {}", materializedMetrics);
        for (MetricDefine md : materializedMetrics) {
          metricDefines.put(md.getName(), md);
        }
      }
    } catch (Exception e) {
      log.info("[apm] load metric definitions from database fail", e);
    }
    updateMetricDefines(metricDefines);
  }

  public List<String> listMetrics() {
    List<String> metrics = new ArrayList<>(this.metricDefines.keySet());
    metrics.sort(String::compareTo);
    return metrics;
  }

  public List<MetricDefine> listMetricDefines() {
    return new ArrayList<>(this.metricDefines.values());
  }

  public List<String> listMaterializedMetrics() {
    List<String> metrics = new ArrayList<>();
    this.metricDefines.forEach((name, metric) -> {
      if (metric.isMaterialized()) {
        metrics.add(name);
      }
    });
    metrics.sort(String::compareTo);
    return metrics;
  }

  public MetricDefine getMetric(String name) {
    return this.metricDefines.get(name);
  }
}
