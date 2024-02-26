/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.common.JsonUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/28
 *
 * @author xzchaoo
 */
@Slf4j
public class XConsoleOutput implements XOutput {
  public static final String TYPE = "CONSOLE";
  private boolean printMultiFields = true;

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public void write(Batch batch) {
    AggTaskKey key = batch.key;
    WindowInfo w = batch.window;
    OutputItem oi = batch.oi;

    String baseName = batch.oi.getName();
    AggStringLookup aggStringLookup = new AggStringLookup();
    boolean useFormatName = baseName.contains("$");
    StringSubstitutor stringSubstitutor = new StringSubstitutor(aggStringLookup);

    for (Group g : batch.getGroups()) {
      Map<String, Object> finalFields = g.finalFields;

      if (printMultiFields) {
        log.info("[agg] [{}] emit name=[{}] ts=[{}] tags=[{}] fields={}", //
            key, //
            oi.getName(), //
            Utils.formatTimeShort(w.getTimestamp()), //
            JsonUtils.toJson(g.getTags()), //
            JsonUtils.toJson(finalFields)); //
      } else {
        for (Map.Entry<String, Object> e : finalFields.entrySet()) {
          String metricName;
          if (useFormatName) {
            aggStringLookup.bind(e.getKey(), g.tags, batch.key.getPartitionInfo());
            metricName = stringSubstitutor.replace(baseName);
          } else {
            metricName = baseName;
          }
          log.info("[agg] [{}] emit name=[{}] ts=[{}] metric=[{}] tags=[{}] value=[{}]", //
              key, //
              oi.getName(), //
              Utils.formatTimeShort(w.getTimestamp()), //
              metricName, //
              JsonUtils.toJson(g.getTags()), //
              e.getValue()); //
        }
      }

    }

  }

}
