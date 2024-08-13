/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.common.model.query.Request;
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
@Api(value = "event", description = "the event API")
@RequestMapping("/cluster/api/v1/event")
public interface EventApi {

  @ApiOperation(value = "query event list", nickname = "queryEvents", notes = "",
      response = Event.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"events",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Event.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/query", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<List<Event>> queryEvents(
      @ApiParam(value = "查询条件。", required = true) @Valid @RequestBody Request request)
      throws Exception;

  @ApiOperation(value = "insert event list", nickname = "insertEvents", notes = "",
      response = Event.class, authorizations = {@Authorization(value = "APIKeyHeader"),
          @Authorization(value = "APIKeyQueryParam")},
      tags = {"insertEvents",})
  @ApiResponses(value = {@ApiResponse(code = 200, message = "请求正常。", response = Boolean.class),
      @ApiResponse(code = 400, message = "请求失败。", response = FailResponse.class)})
  @RequestMapping(value = "/insert", produces = {"application/json"},
      consumes = {"application/json"}, method = RequestMethod.POST)
  ResponseEntity<Boolean> insertEvents(
      @ApiParam(value = "", required = true) @Valid @RequestBody List<Event> events)
      throws Exception;
}
