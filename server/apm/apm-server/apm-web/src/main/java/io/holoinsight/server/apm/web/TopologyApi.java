/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.common.model.query.QueryTopologyRequest;
import io.holoinsight.server.apm.common.model.query.Topology;
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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen")
@Api(value = "topology", description = "the topology API")
@RequestMapping("/cluster/api/v1/topology")
public interface TopologyApi {

  @ApiOperation(value = "query tenant topology", nickname = "queryTenantTopology",
      notes = "查询租户下的拓扑", response = Topology.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"tenantTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/tenantTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryTenantTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;


  @ApiOperation(value = "query service topology", nickname = "queryServiceTopology",
      notes = "查询服务的拓扑", response = Topology.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"serviceTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/serviceTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryServiceTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;


  @ApiOperation(value = "query serviceInstance topology", nickname = "queryServiceInstanceTopology",
      notes = "查询服务实例的拓扑", response = Topology.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"serviceInstanceTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/serviceInstanceTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryServiceInstanceTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;

  @ApiOperation(value = "query endpoint topology", nickname = "queryEndpointTopology",
      notes = "查询接口的拓扑", response = Topology.class, authorizations = {
          @Authorization(value = "APIKeyHeader"), @Authorization(value = "APIKeyQueryParam")},
      tags = {"endpointTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/endpointTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryEndpointTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;


  @ApiOperation(value = "query db topology", nickname = "queryDbTopology", notes = "查询数据库的拓扑",
      response = Topology.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"dbTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/dbTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryDbTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;

  @ApiOperation(value = "query mq topology", nickname = "queryMQTopology", notes = "查询消息队列的拓扑",
      response = Topology.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"mqTopology",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Topology.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query/mqTopology", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Topology> queryMQTopology(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody QueryTopologyRequest request)
      throws Exception;


}
