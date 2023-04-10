/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.postcal;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Component
public class MetricsManager {

  @Value("classpath:metrics/metrics.json")
  private Resource resource;

  @Autowired
  private SuperCacheService superCacheService;

  @Getter
  private Map<String, MetricDefine> metricDefines = new HashMap<>();


  @PostConstruct
  private List<MetricDefine> loadFromResource() throws IOException {
    List<MetricDefine> materializedMetrics;
    String metricDefineJson = IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
    materializedMetrics = GsonUtils.get().fromJson(metricDefineJson,
        new TypeToken<List<MetricDefine>>() {}.getType());
    log.info("[apm] load metric definitions from resource: {}", materializedMetrics);
    materializedMetrics.forEach(materializedMetric -> this.metricDefines
        .put(materializedMetric.getName(), materializedMetric));
    return materializedMetrics;
  }

  /**
   * Metrics defined in the database have higher priority, which can override the content in
   * metrics.json
   */
  @Scheduled(initialDelay = 10000L, fixedDelay = 60000L)
  private void loadFromDB() {
    try {
      MetaDataDictValue metricDefineDictVal = superCacheService.getSc().metaDataDictValueMap
          .getOrDefault("global_config", new HashMap<>()).get("apm_materialized_metrics");
      if (metricDefineDictVal != null) {
        List<MetricDefine> materializedMetrics = GsonUtils.get().fromJson(
            metricDefineDictVal.getDictValue(), new TypeToken<List<MetricDefine>>() {}.getType());
        log.info("[apm] load metric definitions from database: {}", materializedMetrics);
        materializedMetrics.forEach(materializedMetric -> this.metricDefines
            .put(materializedMetric.getName(), materializedMetric));
      }
    } catch (Exception e) {
      log.info("[apm] load metric definitions from database fail", e);
    }
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
