/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.web.model.FailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author jiwliu
 * @version : TraceApi.java, v 0.1 2022年09月20日 15:50 xiangwanpeng Exp $
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2020-01-21T07:37:08.930Z")
@Api(value = "trace", description = "the trace API")
@RequestMapping("/cluster/api/v1/trace")
public interface TraceApi {

  @ApiOperation(value = "query basic traces", nickname = "queryTraces",
      notes = "根据查询条件查询一批trace的基本信息。", response = TraceBrief.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"traces",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = TraceBrief.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/basic", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<TraceBrief> queryBasicTraces(@ApiParam(value = "trace查询条件。",
      required = true) @Valid @RequestBody QueryTraceRequest request) throws Exception;


  @ApiOperation(value = "query trace", nickname = "queryTrace", notes = "根据查询条件查询一批trace的基本信息。",
      response = Trace.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"trace",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Trace.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Trace> queryTrace(@ApiParam(value = "trace查询条件。",
      required = false) @Valid @RequestBody QueryTraceRequest request) throws Exception;

  @ApiOperation(value = "query trace", nickname = "queryTrace", notes = "根据查询条件查询一批trace的基本信息。",
      response = List.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"trace",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Trace.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/traceTree", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<TraceTree>> queryTraceTree(@ApiParam(value = "trace查询条件。",
      required = false) @Valid @RequestBody QueryTraceRequest request) throws Exception;
}
