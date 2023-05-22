/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.*;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.apm.web.model.FailResponse;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : TraceApi.java, v 0.1 2022年09月20日 15:50 xiangwanpeng Exp $
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2020-01-21T07:37:08.930Z")
@Api(value = "trace", description = "the trace API")
@RequestMapping("/cluster/api/v1/metric")
public interface MetricApi {

  @ApiOperation(value = "list metrics", nickname = "listMetrics", notes = "列出所有指标名称。",
      response = List.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"list metrics",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = List.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/list", produces = {"application/json"}, consumes = {"application/json"},
      method = RequestMethod.POST)
  ResponseEntity<List<String>> listMetrics() throws IOException;

  @ApiOperation(value = "list metric defines", nickname = "listMetricDefines", notes = "列出所有指标定义。",
      response = List.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"list metric defines",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = List.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/defines", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<MetricDefine>> listMetricDefines() throws IOException;

  @ApiOperation(value = "query metric data", nickname = "queryMetricData", notes = "查询指标数据",
      response = MetricValues.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"metric",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = MetricValues.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<MetricValues> queryMetricData(@ApiParam(value = "metric查询条件。",
      required = false) @Valid @RequestBody QueryMetricRequest request) throws Exception;

  @ApiOperation(value = "query metric define", nickname = "queryMetricDefine", notes = "查询指标定义",
      response = MetricDefine.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"define",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = MetricDefine.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/define", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<MetricDefine> queryMetricDefine(@ApiParam(value = "metric name",
      required = false) @RequestBody @Valid QueryMetricRequest metric);

  @ApiOperation(value = "query metric schema", nickname = "queryMetricSchema", notes = "查询指标结构",
      response = MetricValues.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"schema",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = List.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/schema", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<String>> queryMetricSchema(@ApiParam(value = "metric name",
      required = false) @RequestBody @Valid QueryMetricRequest metric) throws IOException;

  @ApiOperation(value = "billing", nickname = "billing", notes = "根据条件进行计费。",
      response = StatisticData.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"billing",})
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "请求正常。", response = StatisticData.class),
          @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/billing", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<StatisticData> billing(
      @ApiParam(value = "查询条件。", required = false) @Valid @RequestBody QueryTraceRequest request)
      throws Exception;

  @ApiOperation(value = "statistic", nickname = "statistic", notes = "统计计费。",
      response = StatisticData.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"statistic",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = List.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/statistic", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<StatisticDataList> statistic(
      @ApiParam(value = "查询条件。", required = false) @Valid @RequestBody StatisticRequest request)
      throws Exception;
}
