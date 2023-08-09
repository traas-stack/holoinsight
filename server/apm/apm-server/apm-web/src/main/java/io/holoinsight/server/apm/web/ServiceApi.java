/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.QueryServiceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
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
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen")
@Api(value = "service", description = "the service API")
@RequestMapping("/cluster/api/v1/service")
public interface ServiceApi {

  @ApiOperation(value = "query service list", nickname = "queryServiceList", notes = "查询服务列表",
      response = Service.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"serviceList",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Service.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/serviceList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<Service>> queryServiceList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryServiceRequest request)
      throws Exception;


  @ApiOperation(value = "query service error list", nickname = "queryServiceErrorList",
      notes = "查询服务异常列表", response = Map.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"serviceErrorList",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Service.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/serviceErrorList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<Map<String, String>>> queryServiceErrorList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryServiceRequest request)
      throws Exception;

  @ApiOperation(value = "query service error datail", nickname = "queryServiceErrorDetail",
      notes = "查询服务异常详情", response = Map.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"serviceErrorDetail",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Service.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/serviceErrorDetail", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<Map<String, String>>> queryServiceErrorDetail(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryServiceRequest request)
      throws Exception;
}
