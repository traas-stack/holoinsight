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

  private Map<String, List<String>> composedMetrics = new HashMap<>();

  public static final String SUFFIX_CPM = "_cpm";
  public static final String SUFFIX_CPM_FAIL = "_cpm_fail";
  public static final String SUFFIX_RESP_TIME = "_resp_time";
  // the materialized detailed metrics defined in metrics.json
  public static final String SPAN_MATERIALIZED_CPM = "apm_span_materialized_cpm";
  public static final String SPAN_MATERIALIZED_CPM_FAIL = "apm_span_materialized_cpm_fail";
  public static final String SPAN_MATERIALIZED_RESP_TIME = "apm_span_materialized_resp_time";
  public static final String COMPONENT_MATERIALIZED_CPM = "apm_component_materialized_cpm";
  public static final String COMPONENT_MATERIALIZED_CPM_FAIL =
      "apm_component_materialized_cpm_fail";
  public static final String COMPONENT_MATERIALIZED_RESP_TIME =
      "apm_component_materialized_resp_time";

  @Getter
  private Map<String, MetricDefine> metricDefines = new HashMap<>();

  @PostConstruct
  public void load() throws IOException {
    List<MetricDefine> rsMetrics = loadFromResource(resource);
    if (rsMetrics != null) {
      rsMetrics.forEach(rsMetric -> this.metricDefines.put(rsMetric.getName(), rsMetric));
    }
    List<MetricDefine> dbMetrics = loadFromDB();
    if (dbMetrics != null) {
      dbMetrics.forEach(dbMetric -> this.metricDefines.put(dbMetric.getName(), dbMetric));
    }
    this.metricDefines.forEach((name, metric) -> {
      if (StringUtils.endsWith(name, SUFFIX_CPM)) {
        if (StringUtils.equals(metric.getIndex(), SpanDO.INDEX_NAME)) {
          composedMetrics.put(name, Collections.singletonList(SPAN_MATERIALIZED_CPM));
        } else if (StringUtils.equals(metric.getIndex(), ServiceRelationDO.INDEX_NAME)) {
          composedMetrics.put(name, Collections.singletonList(COMPONENT_MATERIALIZED_CPM));
        }
      } else if (StringUtils.endsWith(name, SUFFIX_CPM_FAIL)) {
        if (StringUtils.equals(metric.getIndex(), SpanDO.INDEX_NAME)) {
          composedMetrics.put(name, Collections.singletonList(SPAN_MATERIALIZED_CPM_FAIL));
        } else if (StringUtils.equals(metric.getIndex(), ServiceRelationDO.INDEX_NAME)) {
          composedMetrics.put(name, Collections.singletonList(COMPONENT_MATERIALIZED_CPM_FAIL));
        }
      } else if (StringUtils.endsWith(name, SUFFIX_RESP_TIME)) {
        if (StringUtils.equals(metric.getIndex(), SpanDO.INDEX_NAME)) {
          composedMetrics.put(name,
              Arrays.asList(SPAN_MATERIALIZED_RESP_TIME, "/", SPAN_MATERIALIZED_CPM));
        } else if (StringUtils.equals(metric.getIndex(), ServiceRelationDO.INDEX_NAME)) {
          composedMetrics.put(name,
              Arrays.asList(COMPONENT_MATERIALIZED_RESP_TIME, "/", COMPONENT_MATERIALIZED_CPM));
        }
      }
    });
    log.info("[apm] load metric definitions: {}", this.metricDefines);
  }

  private List<MetricDefine> loadFromResource(Resource rs) throws IOException {
    List<MetricDefine> materializedMetrics = new ArrayList<>();
    String metricDefineJson = IOUtils.toString(rs.getInputStream(), Charset.defaultCharset());
    materializedMetrics = GsonUtils.get().fromJson(metricDefineJson,
        new TypeToken<List<MetricDefine>>() {}.getType());
    log.info("[apm] load metric definitions from database: {}", materializedMetrics);
    return materializedMetrics;
  }

  private List<MetricDefine> loadFromDB() {
    List<MetricDefine> materializedMetrics = new ArrayList<>();
    MetaDataDictValue metricDefineDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("apm_materialized_metrics");
    if (metricDefineDictVal != null) {
      return GsonUtils.get().fromJson(metricDefineDictVal.getDictValue(),
          new TypeToken<List<MetricDefine>>() {}.getType());
    }
    log.info("[apm] load metric definitions from database: {}", materializedMetrics);
    return materializedMetrics;
  }

  public List<String> listMetrics() {
    List<String> metrics = new ArrayList<>(this.metricDefines.keySet());
    metrics.sort(String::compareTo);
    return metrics;
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

  public List<String> fromMaterializedMetrics(String metric) {
    return composedMetrics.get(metric);
  }

  public MetricDefine getMetric(String name) {
    return this.metricDefines.get(name);
  }
}
