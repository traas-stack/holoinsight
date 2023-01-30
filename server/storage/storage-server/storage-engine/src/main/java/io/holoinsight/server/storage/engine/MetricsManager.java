/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine;

import io.holoinsight.server.storage.common.utils.GsonUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author jiwliu
 * @version : MetricsManager.java, v 0.1 2022年09月29日 17:48 xiangwanpeng Exp $
 */
@Component
public class MetricsManager {

  @Value("classpath:metrics/metrics-span.json")
  private Resource resource;

  @Getter
  private Map<String, MetricDefine> metricDefines = new HashMap<>();

  @PostConstruct
  public void load() throws IOException {
    String metricDefineJson = IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
    List<MetricDefine> metricDefineList = GsonUtils.get().fromJson(metricDefineJson,
        new TypeToken<List<MetricDefine>>() {}.getType());
    if (metricDefineList != null) {
      this.metricDefines = metricDefineList.stream()
          .collect(Collectors.toMap(MetricDefine::getName, Function.identity()));
    }
  }

  public List<String> listMetrics() {
    List<String> metrics = new ArrayList<>(this.metricDefines.keySet());
    metrics.sort(String::compareTo);
    return metrics;
  }

  public MetricDefine getMetric(String name) {
    return this.metricDefines.get(name);
  }
}
