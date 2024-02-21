/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.QueryEndpointRequest;
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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen")
@Api(value = "endpoint", description = "the endpoint API")
@RequestMapping("/cluster/api/v1/endpoint")
public interface EndpointApi {

  @ApiOperation(value = "query endpoint list", nickname = "queryEndpointList", notes = "查询接口列表",
      response = Endpoint.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"endpointList",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Endpoint.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/endpointList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<Endpoint>> queryEndpointList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryEndpointRequest request)
      throws Exception;
}
