/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.model.Header;
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
}
