/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.gson.Gson;
import io.holoinsight.server.extension.promql.model.Endpoint;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinyan.ljw
 * @date 2023/2/21
 */
public class HttpClientUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

  private static final long READ_TIMEOUT_MS = 10000;
  private static final long WRITE_TIMEOUT_MS = 10000;

  private static final MediaType JSON_MEDIA_TYPE =
      MediaType.parse("application/json; charset=utf-8");

  private static final OkHttpClient OK_HTTP_CLIENT =
      new OkHttpClient.Builder().readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
          .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS).build();

  public static OkHttpClient httpClient() {
    return OK_HTTP_CLIENT;
  }

  public static Map<String, String> params(final String key, final String val) {
    return params(key, val, HashMap::new);
  }

  public static Map<String, String> params(final String key, final String val,
      final Supplier<Map<String, String>> ctx) {
    final Map<String, String> map = ctx.get();
    map.put(key, val);
    return map;
  }

  public static RequestBody requestBody(final Map<String, String> params) {
    return RequestBody.create(new Gson().toJson(params), JSON_MEDIA_TYPE);
  }

  public static String get(Endpoint endpoint, String url, Map<String, Object> params) {
    Builder builder = new Builder().url(withParams(endpoint, url, params));
    Request request = builder.build();
    try (Response resp = httpClient().newCall(request).execute()) {
      if (!resp.isSuccessful()) {
        throw new RuntimeException(String.format(
            "Execute url [%s] error from server %s, err_code=%d, err_msg=%s, detail_msg=%s",
            request.url(), endpoint, resp.code(), resp.message(), getRespBody(resp)));
      }
      return getRespBody(resp);
    } catch (final Throwable t) {
      LOGGER.error("Fail to execute url: {}.", request.url(), t);
      throw new RuntimeException(t);
    }
  }

  public static String withParams(Endpoint endpoint, String url, Map<String, Object> params) {
    String host = endpoint.getHost();
    if (!StringUtils.startsWithIgnoreCase(host, "http")
        && !StringUtils.startsWithIgnoreCase(host, "https")) {
      host = "http://" + host;
    }
    StringBuilder urlBuilder =
        new StringBuilder(host).append(":").append(endpoint.getPort()).append(url);
    if (!isEmpty(params)) {
      urlBuilder.append("?");
      params.forEach((k, v) -> {
        urlBuilder.append(k).append("=").append(v).append("&");
      });
    }
    return urlBuilder.substring(0, urlBuilder.length() - 1);
  }

  private static String getRespBody(final Response resp) throws IOException {
    final ResponseBody body = resp.body();
    return body == null ? "" : body.string();
  }

  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }
}
