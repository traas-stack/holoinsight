/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import io.holoinsight.server.agg.v1.dispatcher.AggDispatcher;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.model.Header;
import io.holoinsight.server.extension.model.Record;
import io.holoinsight.server.extension.model.Row;
import io.holoinsight.server.extension.model.Table;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
@Slf4j
public class AggDispatcherMockDataGenerator {
  @Autowired
  private AggDispatcher aggDispatcher;

  @Scheduled(initialDelay = 1000L, fixedDelay = 1000L)
  public void execute() {
    AuthInfo ai = new AuthInfo();
    ai.setTenant("monitor");

    long ts = System.currentTimeMillis() / 1000 * 1000;

    Random r = new Random();

    Table table = new Table();

    Header header = new Header();
    header.setTagKeys(Arrays.asList("app", "userId"));
    header.setFieldKeys(Arrays.asList("count", "count1"));
    table.setHeader(header);

    table.setRows(new ArrayList<>(100));

    table.setName("test");
    table.setTimestamp(ts);

    for (int i = 0; i < 100; i++) {
      Row row = new Row();
      row.setTimestamp(ts);

      String app = "foo" + (r.nextInt(5));
      String userId = "user" + (r.nextInt(100));
      row.setTagValues(Arrays.asList(app, userId));

      row.setFieldValues(Arrays.asList(1D, 1D));

      table.getRows().add(row);
    }

    aggDispatcher.dispatchDetailData(ai, table);
  }

  @Scheduled(initialDelay = 1000L, fixedDelay = 1000L)
  public void execute_analysis_known() {
    AuthInfo ai = new AuthInfo();
    ai.setTenant("monitor");

    List<Record> records = new ArrayList<>();
    String name = "loganalysis";
    {
      Record r = new Record();
      r.setName(name);
      r.setTimestamp(System.currentTimeMillis());
      Map<String, String> tags = new HashMap<>();
      tags.put("hostname", "hostname-1");
      tags.put("eventName", "test-1");
      r.setTags(tags);

      String value;
      {
        List<AnalyzedLog> logs = new ArrayList<>();

        AnalyzedLog log1 = new AnalyzedLog();
        log1.setCount(2);
        log1.setSample("2024-02-26 18:18:18 [INFO] hello world, cost=[100]");

        logs.add(log1);

        value = JsonUtils.toJson(Collections.singletonMap("analyzedLogs", logs));
      }

      Map<String, Object> fields = new HashMap<>();
      fields.put("value", value);
      r.setFields(fields);
      records.add(r);
    }
    {
      Record r = new Record();
      r.setName(name);
      r.setTimestamp(System.currentTimeMillis());
      Map<String, String> tags = new HashMap<>();
      tags.put("hostname", "hostname-2");
      tags.put("eventName", "test-1");
      r.setTags(tags);

      String value;
      {
        List<AnalyzedLog> logs = new ArrayList<>();

        AnalyzedLog log1 = new AnalyzedLog();
        log1.setCount(3);
        log1.setSample("2024-02-26 18:18:18 [INFO] hello world, cost=[100]");

        logs.add(log1);

        value = JsonUtils.toJson(Collections.singletonMap("analyzedLogs", logs));
      }

      Map<String, Object> fields = new HashMap<>();
      fields.put("value", value);
      r.setFields(fields);
      records.add(r);
    }
    {
      Record r = new Record();
      r.setName(name);
      r.setTimestamp(System.currentTimeMillis());
      Map<String, String> tags = new HashMap<>();
      tags.put("app", "testapp");
      tags.put("hostname", "hostname-3");
      tags.put("eventName", "__analysis");
      r.setTags(tags);

      String value;
      {
        List<AnalyzedLog> logs = new ArrayList<>();

        AnalyzedLog log1 = new AnalyzedLog();
        List<LAPart> parts = new ArrayList<>();
        parts.add(new LAPart("UNKNOWN", false, false, 1));
        parts.add(new LAPart("hello world", false, true, 1));
        parts.add(new LAPart("cost", false, false, 1));
        parts.add(new LAPart("100", false, false, 1));
        log1.setParts(parts);
        log1.setCount(3);
        List<SourceWord> sourceWords = new ArrayList<>();
        sourceWords.add(new SourceWord("1.1.1.1", 1));
        sourceWords.add(new SourceWord("2.2.2.2", 1));
        log1.setSourceWords(sourceWords);
        log1.setSample("2024-02-26 18:18:18 [UNKNOWN] hello world, cost=[100]");

        logs.add(log1);

        value = JsonUtils.toJson(Collections.singletonMap("analyzedLogs", logs));
      }

      Map<String, Object> fields = new HashMap<>();
      fields.put("value", value);
      r.setFields(fields);
      records.add(r);
    }

    aggDispatcher.dispatchRecords(ai, name, records);
  }
}
