/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryResult;
import io.holoinsight.server.extension.model.QueryResult.Result;
import io.holoinsight.server.extension.promql.model.Endpoint;
import io.holoinsight.server.extension.promql.model.PqlResult;
import io.holoinsight.server.extension.promql.model.PqlResult.Data;
import io.holoinsight.server.extension.promql.utils.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author jiwliu
 * @date 2023/3/8
 */
public class HttpPqlQueryService implements PqlQueryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpPqlQueryService.class);

  public static final String METRIC_NAME = "__name__";
  public static final String VECTOR_TYPE = "vector";
  public static final String MATRIX_TYPE = "matrix";
  public static final String SUCCESS = "success";
  public static final String DEFAULT_TENANT = "default";
  public static final String API_V1_QUERY_RANGE = "api/v1/query_range";
  public static final String API_V1_QUERY = "api/v1/query";
  private Map<String, Endpoint> endpoints;

  public HttpPqlQueryService(RemotePqlProperties remotePqlProperties) {
    this.endpoints = remotePqlProperties.endpoints;
  }

  @Override
  public List<Result> query(PqlParam pqlParam) {
    String tenant = pqlParam.getTenant();
    Endpoint endpoint = getEndpoint(tenant);
    if (Objects.isNull(endpoint)) {
      LOGGER.error("[PqlRemoteQuery] query error, tenant {} endpoint is not exists", tenant);
      return Lists.newArrayList();
    }
    String body = HttpClientUtils.get(endpoint, API_V1_QUERY, parasParam(pqlParam));
    return toResult(pqlParam, body);
  }

  private Endpoint getEndpoint(String tenant) {
    return Objects.isNull(endpoints.get(tenant)) ? endpoints.get(DEFAULT_TENANT)
        : endpoints.get(tenant);
  }

  private Map<String, Object> parasParam(PqlParam pqlParam) {
    HashMap<String, Object> params = Maps.newHashMap();
    try {
      params.put("query", URLEncoder.encode(pqlParam.getQuery(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("[PqlRemoteQuery] encode pql query param error , pql : {}", pqlParam.getQuery(),
          e);
      throw new RuntimeException("encode pql query param error");
    }
    long time = pqlParam.getTime();
    if (time > 0) {
      params.put("time", unixTimestamp(time));
    }
    String timeoutStr = pqlParam.getTimeout();
    if (StringUtils.isNotBlank(timeoutStr)) {
      String timeout = timeoutStr.substring(0, timeoutStr.length() - 1);
      params.put("timeout", Integer.parseInt(timeout));
    }
    if (pqlParam.getStep() > 0) {
      params.put("step", pqlParam.getStep());
    }
    if (pqlParam.getStart() > 0) {
      params.put("start", unixTimestamp(pqlParam.getStart()));
    }
    if (pqlParam.getEnd() > 0) {
      params.put("end", unixTimestamp(pqlParam.getEnd()));
    }
    return params;
  }

  /**
   * @param body
   * @return
   */
  private List<Result> toResult(PqlParam pqlParam, String body) {
    List<Result> results = Lists.newArrayList();
    if (StringUtils.isBlank(body)) {
      return results;
    }
    PqlResult pqlResult = new Gson().fromJson(body, PqlResult.class);
    if (!StringUtils.equalsIgnoreCase(SUCCESS, pqlResult.getStatus())) {
      LOGGER.error("[PqlRemoteQuery] exec pql: {} error, body:{}", pqlParam.getQuery(), body);
      return results;
    }
    Data data = pqlResult.getData();
    if (Objects.isNull(data)) {
      LOGGER.error("[PqlRemoteQuery] exec pql: {} get empty data, resp body:{}",
          pqlParam.getQuery(), body);
      return results;
    }
    List<Map<String, Object>> items = data.getResult();
    if (CollectionUtils.isEmpty(items)) {
      LOGGER.error("[PqlRemoteQuery] exec pql: {} get empty result, resp body:{}",
          pqlParam.getQuery(), body);
      return results;
    }
    items.forEach(item -> {
      Result result = new Result();
      Map<String, String> metric = (Map) item.get("metric");
      if (!CollectionUtils.isEmpty(metric)) {
        result.setMetric(metric.get(METRIC_NAME));
      }
      result.setTags(metric);
      if (StringUtils.equalsIgnoreCase(VECTOR_TYPE, data.getResultType())) {
        List<List<Object>> value = (List) item.get("value");
        QueryResult.Point point = getPoint(value);
        result.setPoints(Lists.newArrayList(Lists.newArrayList(point)));
      } else if (StringUtils.equalsIgnoreCase(MATRIX_TYPE, data.getResultType())) {
        List<List<Object>> values = (List) item.get("values");
        ArrayList<QueryResult.Point> points = Lists.newArrayList();
        values.forEach(val -> {
          QueryResult.Point point = getPoint(val);
          points.add(point);
        });
        result.setPoints(Lists.newArrayList(points));
      }
      results.add(result);
    });
    return results;
  }

  private QueryResult.Point getPoint(List value) {
    QueryResult.Point point = new QueryResult.Point();
    double value0 = (double) value.get(0);
    point.setTimestamp(Math.round(value0) * 1000);
    point.setStrValue(value.get(1).toString());
    return point;
  }

  private static double unixTimestamp(long ms) {
    if (ms == 0) {
      ms = System.currentTimeMillis();
    }
    return (double) ms / 1000;
  }

  @Override
  public List<Result> queryRange(PqlParam pqlParam) {
    String tenant = pqlParam.getTenant();
    Endpoint endpoint = getEndpoint(tenant);
    if (Objects.isNull(endpoint)) {
      LOGGER.error("[PqlRemoteQuery] queryRange error, tenant {} endpoint is not exists", tenant);
      return Lists.newArrayList();
    }
    String body = HttpClientUtils.get(endpoint, API_V1_QUERY_RANGE, parasParam(pqlParam));
    return toResult(pqlParam, body);
  }

  public void addEndpoints(String tenant, Endpoint endpoint) {
    this.endpoints.put(tenant, endpoint);
  }
}
