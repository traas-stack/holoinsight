/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.QueryComponentRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
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
@Api(value = "component", description = "the component API")
@RequestMapping("/cluster/api/v1/component")
public interface VirtualComponentApi {

  @ApiOperation(value = "query db list", nickname = "queryDbList", notes = "查询数据库列表",
      response = Service.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"dbList",})
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "请求正常。", response = VirtualComponent.class),
          @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/dbList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<VirtualComponent>> queryDbList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryComponentRequest request)
      throws Exception;


  @ApiOperation(value = "query cache list", nickname = "queryCacheList", notes = "查询缓存列表",
      response = Service.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"cacheList",})
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "请求正常。", response = VirtualComponent.class),
          @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/cacheList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<VirtualComponent>> queryCacheList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryComponentRequest request)
      throws Exception;


  @ApiOperation(value = "query mq list", nickname = "queryMQList", notes = "查询消息队列列表",
      response = Service.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"cacheList",})
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "请求正常。", response = VirtualComponent.class),
          @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/mqList", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<VirtualComponent>> queryMQList(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryComponentRequest request)
      throws Exception;


  @ApiOperation(value = "query trace list", nickname = "queryTraceList", notes = "查询调用链列表",
      response = List.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"traceList",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = List.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/componentTraceIds", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<String>> queryComponentTraceIds(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryComponentRequest request)
      throws Exception;

}
