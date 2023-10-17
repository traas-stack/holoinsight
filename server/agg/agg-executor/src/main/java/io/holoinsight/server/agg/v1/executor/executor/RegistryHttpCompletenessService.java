/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.ExpectedCompleteness;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
@Slf4j
public class RegistryHttpCompletenessService implements CompletenessService {

  private final OkHttpClient client;

  private final Cache<String, ExpectedCompleteness> cache = CacheBuilder.newBuilder() //
      .expireAfterWrite(Duration.ofMinutes(2)) //
      .build(); //

  public RegistryHttpCompletenessService() {
    client = new OkHttpClient.Builder().build();
  }

  @Override
  public ExpectedCompleteness getByCollectTableName(String tableName) {
    ExpectedCompleteness cached = cache.getIfPresent(tableName);
    if (cached != null) {
      return cached;
    }

    String url =
        "http://127.0.0.1:8080/internal/api/registry/target/listDimsByTemplate?t=" + tableName;
    Request request = new Request.Builder() //
        .get() //
        .url(url) //
        .build();

    try (Response resp = client.newCall(request).execute()) {
      ExpectedCompleteness ec = parse0(resp);
      cache.put(tableName, ec);
      return ec;
    } catch (IOException e) {
      log.error("getByCollectTableName error", e);
      return null;
    }
  }

  @Override
  public ExpectedCompleteness getByDimTable(String dimTable) {
    ExpectedCompleteness cached = cache.getIfPresent(dimTable);
    if (cached != null) {
      return cached;
    }

    String url = "http://127.0.0.1:8080/internal/api/registry/dim/queryAll?tableName=" + dimTable;
    Request request = new Request.Builder() //
        .get() //
        .url(url) //
        .build();

    try (Response resp = client.newCall(request).execute()) {
      ExpectedCompleteness ec = parse0(resp);
      cache.put(dimTable, ec);
      return ec;
    } catch (IOException e) {
      log.error("getByCollectTableName error", e);
      return null;
    }
  }

  private ExpectedCompleteness parse0(Response resp) throws IOException {
    String content = resp.body().string();

    JSONObject r = JSON.parseObject(content);
    if (r.getBooleanValue("success")) {
      ExpectedCompleteness c = new ExpectedCompleteness();
      JSONArray a = r.getJSONArray("data");
      List<Map<String, Object>> targets = new ArrayList<>(a.size());

      for (int i = 0; i < a.size(); i++) {
        targets.add(a.getJSONObject(i));
      }
      c.setExpectedTargets(targets);
      return c;
    } else {
      log.error("parse0 error {}", content);
    }

    return null;
  }

}
