/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.gateway.core.grpc.GatewayHook;
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

    List<GatewayHook.Data> request = new ArrayList<>();

    long ts = System.currentTimeMillis() / 1000 * 1000;

    Random r = new Random();
    for (int i = 0; i < 100; i++) {
      GatewayHook.Data data = new GatewayHook.Data();
      data.setName("test");
      data.setTimestamp(ts);
      Map<String, String> tags = new HashMap<>();
      tags.put("app", "foo" + (r.nextInt(5)));
      tags.put("userId", "user" + (r.nextInt(100)));
      data.setTags(tags);
      Map<String, Double> fields = new HashMap<>();
      fields.put("count1", 1D);
      fields.put("count", 1D);
      data.setFields(fields);
      request.add(data);
    }
    aggDispatcher.dispatchDetailData(ai, request);
  }
}
