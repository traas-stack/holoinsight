/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web;

import io.holoinsight.server.storage.common.model.query.MetricValues;
import io.holoinsight.server.storage.common.model.query.QueryMetricRequest;
import io.holoinsight.server.storage.web.model.FailResponse;
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
 * @version : TraceApi.java, v 0.1 2022年09月20日 15:50 wanpeng.xwp Exp $
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2020-01-21T07:37:08.930Z")
@Api(value = "trace", description = "the trace API")
@RequestMapping("/api/v1/metric")
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

  @ApiOperation(value = "query metric data", nickname = "queryMetricData", notes = "查询指标数据",
      response = MetricValues.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"metric",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = MetricValues.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<MetricValues> queryMetricData(@ApiParam(value = "metric查询条件。",
      required = false) @Valid @RequestBody QueryMetricRequest request) throws IOException;

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
}
