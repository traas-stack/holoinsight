/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.OutputField;
import io.holoinsight.server.agg.v1.executor.output.AggStringLookup;
import io.holoinsight.server.agg.v1.executor.output.MergedCompleteness;
import io.holoinsight.server.agg.v1.executor.output.PercentileFinalValues;
import io.holoinsight.server.agg.v1.executor.output.XOutput;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xiangfeng.xzc
 */
@Slf4j
public class MetricStorageOutput implements XOutput {
  @Autowired
  private MetricStorage metricStorage;

  @Override
  public String type() {
    return "TSDB";
  }

  @Override
  public void write(Batch batch) {
    WriteMetricsParam param = new WriteMetricsParam();
    param.setTenant(batch.key.getTenant());
    List<WriteMetricsParam.Point> points = new ArrayList<>(batch.groups.size());
    param.setPoints(points);

    long time0 = System.currentTimeMillis();

    String baseName = batch.oi.getName();
    AggStringLookup aggStringLookup = new AggStringLookup();
    boolean useFormatName = baseName.contains("$");
    StringSubstitutor stringSubstitutor = new StringSubstitutor(aggStringLookup);

    String ts = Utils.formatTimeShort(batch.window.getTimestamp());

    Map<String, OutputField> fieldMap = convertToMap(batch.oi.getFields());

    boolean debug = false;

    int discard = 0;
    Set<String> usedMetricNames = new HashSet<>();
    for (Group g : batch.groups) {
      Map<String, Object> finalFields = g.getFinalFields();
      for (Map.Entry<String, Object> e : finalFields.entrySet()) {
        OutputField outputField = fieldMap.get(e.getKey());
        Object value = e.getValue();
        if (value == null) {
          continue;
        }

        String metricName;
        if (useFormatName) {
          aggStringLookup.bind(e.getKey(), g.tags, batch.key.getPartitionInfo());
          metricName = stringSubstitutor.replace(baseName);
        } else {
          metricName = baseName;
        }

        usedMetricNames.add(metricName);
        try {
          if (outputField != null && OutputField.PERCENTILE.equals(outputField.getType())) {
            PercentileFinalValues pfv =
                JSON.parseObject((String) value, PercentileFinalValues.class);
            for (PercentileFinalValues.PercentileFinalValue v : pfv.getValues()) {


              WriteMetricsParam.Point p = new WriteMetricsParam.Point();
              p.setMetricName(metricName);
              p.setTimeStamp(batch.window.getTimestamp());

              // add extra tag 'percent'
              Map<String, String> tags = new HashMap<>(g.getTags().asMap());
              tags.put("percent", Integer.toString((int) (100 * v.getRank())));
              p.setTags(tags);

              p.setValue(v.getQuantile());

              points.add(p);
            }
            continue;
          }

          if (value instanceof Number) {
          } else if (value instanceof String) {
          } else {
            value = JSON.toJSONString(value);
          }

          WriteMetricsParam.Point p = new WriteMetricsParam.Point();
          p.setMetricName(metricName);
          p.setTimeStamp(batch.window.getTimestamp());
          p.setTags(g.getTags().asMap());

          if (value instanceof Number) {
            p.setValue(((Number) value).doubleValue());
          } else {
            p.setStrValue(value.toString());
          }

          if (debug) {
            log.info("[output] [ceresdb3] debug agg=[{}] ts=[{}] metric=[{}] tags=[{}] value=[{}]", //
                batch.key, ts, metricName, g.getTags(), value);
          }
          points.add(p);
        } catch (Exception ex) {
          discard++;
          log.error("[output] [MetricStorage] agg=[{}] ts=[{}] metric=[{}] error", //
              batch.key, ts, metricName, ex);
        }
      }
    }

    if (points.size() > 0) {
      metricStorage.write(param).block();
    }
    long time1 = System.currentTimeMillis();

    log.info("[output] [ceresdb3] agg=[{}] ts=[{}] stat={} output=[{}] discard=[{}] cost=[{}]", //
        batch.key, ts, batch.window.getStat(), points.size(), discard, time1 - time0);

    if (batch.window.preview) {
      return;
    }

    param = new WriteMetricsParam();
    param.setTenant(batch.key.getTenant());
    param.setPoints(points);
    points.clear();
    // completeness info
    for (String metricName : usedMetricNames) {
      String completeMetric = metricName + "_complete";

      for (MergedCompleteness.GroupCompleteness mgc : batch.window.mergedCompleteness
          .getDetails()) {

        String value = JSON.toJSONString(mgc.getTables(), SerializerFeature.NotWriteDefaultValue);

        WriteMetricsParam.Point p = new WriteMetricsParam.Point();
        p.setMetricName(completeMetric);
        p.setTimeStamp(batch.window.getTimestamp());
        p.setTags(mgc.getTags());
        p.setStrValue(value);

        points.add(p);
      }
    }
    if (points.size() > 0) {
      metricStorage.write(param).block();
      long time2 = System.currentTimeMillis();

      log.info(
          "[output] [ceresdb3] agg=[{}] ts=[{}] write completeness info successfully, cost=[{}]", //
          batch.key, ts, time2 - time1);

    }
  }

  private static Map<String, OutputField> convertToMap(List<OutputField> list) {
    return list.stream().collect(Collectors.toMap(OutputField::getName, x -> x, (a, b) -> a));
  }
}
