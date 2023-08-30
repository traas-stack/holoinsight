/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.server.controllers;

import io.holoinsight.server.common.SSRFUtils;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.query.grpc.QueryProto.QueryResponse;
import io.holoinsight.server.query.service.QueryException;
import io.holoinsight.server.query.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author xiangwanpeng
 */
@RestController
@RequestMapping("/cluster/api/v1/query")
public class QueryController {

  @Autowired
  private QueryService queryService;

  @Resource
  private TenantOpsMapper tenantOpsMapper;

  @PostMapping(path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryData(@RequestBody QueryProto.QueryRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryData(request));
    } catch (QueryException e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryTags(@RequestBody QueryProto.QueryRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryTags(request));
    } catch (QueryException e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @DeleteMapping(path = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteTags(@RequestBody QueryProto.QueryRequest request) {
    try {
      return ResponseEntity.ok(queryService.deleteKeys(request));
    } catch (QueryException e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> querySchema(@RequestBody QueryProto.QueryRequest request) {
    try {
      return ResponseEntity.ok(queryService.querySchema(request));
    } catch (QueryException e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> querySchema(@RequestBody QueryProto.QueryMetricsRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryMetrics(request));
    } catch (QueryException e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/tenants", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryTenant(@RequestBody QueryProto.QueryMetricsRequest request) {
    try {
      return ResponseEntity.ok(tenantOpsMapper.selectList(null));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/pql/instant", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> pqlInstantQuery(@RequestBody QueryProto.PqlInstantRequest request) {
    try {
      SSRFUtils.hookStart();
      ResponseEntity<QueryResponse> ok = ResponseEntity.ok(queryService.pqlInstantQuery(request));
      SSRFUtils.hookStop();
      return ok;
    } catch (QueryException e) {
      SSRFUtils.hookStop();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/pql/range", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> pqlRangeQuery(@RequestBody QueryProto.PqlRangeRequest request) {
    try {
      SSRFUtils.hookStart();
      ResponseEntity<QueryResponse> ok = ResponseEntity.ok(queryService.pqlRangeQuery(request));
      SSRFUtils.hookStop();
      return ok;
    } catch (QueryException e) {
      SSRFUtils.hookStop();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/trace/basic", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryBasicTraces(@RequestBody QueryProto.QueryTraceRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryBasicTraces(request));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/trace/query", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryTrace(@RequestBody QueryProto.QueryTraceRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryTrace(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/trace/query/traceTree", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryTraceTree(@RequestBody QueryProto.QueryTraceRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryTraceTree(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/serviceList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryServiceList(@RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryServiceList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/endpointList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryEndpointList(@RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryEndpointList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/serviceInstanceList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryServiceInstanceList(
      @RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryServiceInstanceList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/componentList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryComponentList(@RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryComponentList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/componentTraceIds", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryComponentTraceIds(
      @RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryComponentTraceIds(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/topology", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryTopology(@RequestBody QueryProto.QueryTopologyRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryTopology(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/slowSqlList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> querySlowSqlList(@RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.querySlowSqlList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/serviceErrorList", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryServiceErrorList(@RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryServiceErrorList(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/serviceErrorDetail", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryServiceErrorDetail(
      @RequestBody QueryProto.QueryMetaRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryServiceErrorDetail(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

  @PostMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> queryEvents(@RequestBody QueryProto.QueryEventRequest request) {
    try {
      return ResponseEntity.ok(queryService.queryEvents(request));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
  }

}
