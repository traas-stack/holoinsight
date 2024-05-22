/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.prometheus;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.Point;
import io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest;
import io.holoinsight.server.query.grpc.QueryProto.PqlLabelRequest;
import io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest;
import io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest.Builder;
import io.holoinsight.server.query.grpc.QueryProto.QueryLabelsResponse;
import io.holoinsight.server.query.grpc.QueryProto.QueryResponse;
import io.holoinsight.server.query.grpc.QueryProto.QuerySeriesResponse;
import io.holoinsight.server.query.grpc.QueryServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.DELETE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: GrafanaController.java, Date: 2024-05-20 Time: 17:34
 */
@Slf4j
@RestController
@RequestMapping("/webapi/prometheus")
public class PromController {

  @GrpcClient("queryService")
  private QueryServiceGrpc.QueryServiceBlockingStub queryServiceBlockingStub;

  @Autowired
  private TenantInitService tenantInitService;

  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Service is health");
  }

  /**
   * Instant queries(即时查询)
   * 
   * @param query
   * @param time
   * @param timeout
   * @return
   */
  @GetMapping("/api/v1/query")
  public PromResponseInfo<PromDataInfo> query(@RequestParam(value = "query") String query,
      @RequestParam(value = "time", required = false) String time,
      @RequestParam(value = "timeout", required = false) String timeout,
      @RequestParam(value = "lookback-delta", required = false) String delta) {
    PqlInstantRequest.Builder builder = PqlInstantRequest.newBuilder().setQuery(query)
        .setTenant(tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
        .setTime(convertMsTime(Double.valueOf(time).longValue()));
    if (StringUtils.isNotBlank(timeout)) {
      builder.setTimeout(timeout);
    }

    if (StringUtils.isNotBlank(delta)) {
      builder.setDelta(delta);
    }

    QueryProto.PqlInstantRequest instantRequest = builder.build();
    QueryProto.QueryResponse res = queryServiceBlockingStub.pqlInstantQuery(instantRequest);

    PromResponseInfo<PromDataInfo> responseInfo = new PromResponseInfo<>();
    responseInfo.setStatus("success");
    PromDataInfo promDataInfo = new PromDataInfo();
    promDataInfo.setResultType("vector");

    List<PromResultInfo> promResultInfos = new ArrayList<>();
    for (QueryProto.Result result : res.getResultsList()) {
      PromResultInfo r = new PromResultInfo();
      r.setMetric(result.getTagsMap());

      if (!CollectionUtils.isEmpty(result.getPointsList())) {
        Point p = result.getPointsList().get(0);
        r.setValue(new Object[] {convertSecTime(p.getTimestamp()), p.getStrValue()});

      }
      promResultInfos.add(r);
    }
    promDataInfo.setResult(promResultInfos);
    responseInfo.setData(promDataInfo);
    return responseInfo;
  }

  @GetMapping("/api/v1/query_range")
  public PromResponseInfo<PromDataInfo> queryRange(@RequestParam(value = "query") String query,
      @RequestParam(value = "start") long start, @RequestParam(value = "end") long end,
      @RequestParam(value = "step") String step,
      @RequestParam(value = "timeout", required = false) String timeout,
      @RequestParam(value = "lookback-delta", required = false) String delta) {
    Builder builder = PqlRangeRequest.newBuilder().setQuery(query)
        .setTenant(tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
        .setStart(convertMsTime(start)).setEnd(convertMsTime(end)).setStep(timeStepsConvert(step));

    if (StringUtils.isNotBlank(timeout)) {
      builder.setTimeout(timeout);
    }

    if (StringUtils.isNotBlank(delta)) {
      builder.setDelta(delta);
    }

    QueryProto.PqlRangeRequest rangeRequest = builder.build();
    QueryResponse res = queryServiceBlockingStub.pqlRangeQuery(rangeRequest);
    PromResponseInfo<PromDataInfo> responseInfo = new PromResponseInfo<>();
    responseInfo.setStatus("success");
    PromDataInfo promDataInfo = new PromDataInfo();
    promDataInfo.setResultType("matrix");

    List<PromResultInfo> promResultInfos = new ArrayList<>();
    for (QueryProto.Result result : res.getResultsList()) {
      PromResultInfo r = new PromResultInfo();
      r.setMetric(result.getTagsMap());

      List<Object[]> values = new ArrayList<>();
      if (!CollectionUtils.isEmpty(result.getPointsList())) {
        for (Point p : result.getPointsList()) {
          values.add(new Object[] {convertSecTime(p.getTimestamp()), p.getStrValue()});
        }
      }
      r.setValues(values);
      promResultInfos.add(r);
    }
    promDataInfo.setResult(promResultInfos);
    responseInfo.setData(promDataInfo);
    return responseInfo;
  }

  @GetMapping("/api/v1/series")
  public PromResponseInfo<List<Map<String, String>>> series(
      @RequestParam("match[]") List<String> matches, @RequestParam(value = "start") long start,
      @RequestParam(value = "end") long end) {
    PqlLabelRequest.Builder builder = PqlLabelRequest.newBuilder()
        .setTenant(tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
        .setStart(convertMsTime(start)).setEnd(convertMsTime(end));
    builder.addAllMatch(matches);

    QueryProto.PqlLabelRequest pqlLabelRequest = builder.build();
    QuerySeriesResponse res = queryServiceBlockingStub.pqlSeriesQuery(pqlLabelRequest);

    PromResponseInfo<List<Map<String, String>>> responseInfo = new PromResponseInfo<>();
    responseInfo.setStatus("success");

    List<Map<String, String>> results = new ArrayList<>();
    for (QueryProto.SeriesResult seriesResult : res.getResultsList()) {
      results.add(seriesResult.getResultMap());
    }
    responseInfo.setData(results);
    return responseInfo;
  }

  @DELETE("/api/v1/series")
  public PromResponseInfo<Map<String, Long>> delSeries(
      @RequestParam(value = "match[]") String[] matches) {
    return null;
  }

  @GetMapping("/api/v1/labels")
  public PromResponseInfo<List<String>> labels(
      @RequestParam(value = "match[]", required = false) List<String> matches,
      @RequestParam(value = "start") long start, @RequestParam(value = "end") long end) {
    PqlLabelRequest.Builder builder = PqlLabelRequest.newBuilder()
        .setTenant(tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
        .setStart(convertMsTime(start)).setEnd(convertMsTime(end));
    if (null != matches) {
      builder.getMatchList().addAll(matches);
    }

    QueryProto.PqlLabelRequest pqlLabelRequest = builder.build();
    QueryLabelsResponse res = queryServiceBlockingStub.pqlLabelsQuery(pqlLabelRequest);

    PromResponseInfo<List<String>> responseInfo = new PromResponseInfo<>();
    responseInfo.setStatus("success");
    List<String> results = new ArrayList<>(res.getResultsList());
    responseInfo.setData(results);
    return responseInfo;
  }

  @GetMapping("/api/v1/label/{label_name}/values")
  public PromResponseInfo<List<String>> label(@PathVariable(value = "label_name") String label,
      @RequestParam(value = "match[]", required = false) List<String> matches,
      @RequestParam(value = "start") long start, @RequestParam(value = "end") long end) {
    PqlLabelRequest.Builder builder = PqlLabelRequest.newBuilder()
        .setTenant(tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
        .setStart(convertMsTime(start)).setEnd(convertMsTime(end)).setLabelName(label);
    if (null != matches) {
      builder.getMatchList().addAll(matches);
    }

    QueryProto.PqlLabelRequest pqlLabelRequest = builder.build();
    QueryLabelsResponse res = queryServiceBlockingStub.pqlLabelValuesQuery(pqlLabelRequest);

    PromResponseInfo<List<String>> responseInfo = new PromResponseInfo<>();
    responseInfo.setStatus("success");
    List<String> results = new ArrayList<>(res.getResultsList());
    responseInfo.setData(results);
    return responseInfo;
  }

  @GetMapping("/api/v1/rules")
  public PromResponseInfo<Object> rules() {
    return null;
  }

  @GetMapping("/api/v1/metadata")
  public PromResponseInfo<Object> metadata() {
    return null;
  }

  @GetMapping("/api/v1/status/buildinfo")
  public PromResponseInfo<Object> buildInfo() {
    return null;
  }

  private long timeStepsConvert(String step) {
    if (step.endsWith("ms")) {
      return Integer.parseInt(step.substring(0, step.length() - 2));
    } else if (step.endsWith("s")) {
      return Integer.parseInt(step.substring(0, step.length() - 1)) * 1000L;
    } else if (step.endsWith("m")) {
      return Integer.parseInt(step.substring(0, step.length() - 1)) * 60 * 1000L;
    } else if (step.endsWith("h")) {
      return Integer.parseInt(step.substring(0, step.length() - 1)) * 60 * 60 * 1000L;
    } else if (step.endsWith("d")) {
      return Integer.parseInt(step.substring(0, step.length() - 1)) * 24 * 60 * 60 * 1000L;
    } else if (step.endsWith("w")) {
      return Integer.parseInt(step.substring(0, step.length() - 1)) * 7 * 24 * 60 * 60 * 1000L;
    }
    return 60 * 1000L;
  }

  private long convertMsTime(long time) {
    return time * 1000L;
  }

  private long convertSecTime(long time) {
    return time / 1000L;
  }
}
