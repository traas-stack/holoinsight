/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
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

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public void write(Batch batch) {
    AggTaskKey key = batch.key;
    WindowInfo w = batch.window;
    OutputItem oi = batch.oi;
    for (Group g : batch.getGroups()) {
      Map<String, Object> finalFields = g.finalFields;
      log.info("[agg] [{}] emit name=[{}] ts=[{}] tags=[{}] fields={}", //
          key, //
          oi.getName(), //
          Utils.formatTimeShort(w.getTimestamp()), //
          g.getTags(), //
          JSON.toJSONString(finalFields)); //
    }
  }

}
