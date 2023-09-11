/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.apm;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.QueryComponentRequest;
import io.holoinsight.server.apm.common.model.query.QueryEndpointRequest;
import io.holoinsight.server.apm.common.model.query.QueryMetricRequest;
import io.holoinsight.server.apm.common.model.query.QueryServiceInstanceRequest;
import io.holoinsight.server.apm.common.model.query.QueryServiceRequest;
import io.holoinsight.server.apm.common.model.query.QueryTopologyRequest;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.Request;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.query.StatisticRequest;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ApmAPI {

  @POST("/cluster/api/v1/trace/query/basic")
  Call<TraceBrief> queryBasicTraces(@Body QueryTraceRequest request);

  @POST("/cluster/api/v1/trace/query")
  Call<Trace> queryTrace(@Body QueryTraceRequest request);

  @POST("/cluster/api/v1/trace/query/traceTree")
  Call<List<TraceTree>> queryTraceTree(@Body QueryTraceRequest request);

  @POST("/cluster/api/v1/metric/billing")
  Call<StatisticData> billing(@Body QueryTraceRequest request);

  @POST("/cluster/api/v1/metric/statistic")
  Call<StatisticDataList> statistic(@Body StatisticRequest request);

  @POST("/cluster/api/v1/metric/list")
  @Headers({"Content-Type: application/json", "Accept: application/json"})
  Call<List<String>> listMetrics();

  @POST("/cluster/api/v1/metric/defines")
  @Headers({"Content-Type: application/json", "Accept: application/json"})
  Call<List<MetricDefine>> listMetricDefines();

  @POST("/cluster/api/v1/metric/define/{name}")
  Call<MetricDefine> getMetricDefine(@Path("name") String name);

  @POST("/cluster/api/v1/metric/schema")
  @Headers({"Content-Type: application/json", "Accept: application/json"})
  Call<List<String>> querySchema(@Body QueryMetricRequest request);

  @POST("/cluster/api/v1/metric/query")
  Call<MetricValues> queryMetricData(@Body QueryMetricRequest request);

  @POST("/cluster/api/v1/service/query/serviceList")
  Call<List<Service>> queryServiceList(@Body QueryServiceRequest request);

  @POST("/cluster/api/v1/endpoint/query/endpointList")
  Call<List<Endpoint>> queryEndpointList(@Body QueryEndpointRequest request);

  @POST("/cluster/api/v1/serviceInstance/query/serviceInstanceList")
  Call<List<ServiceInstance>> queryServiceInstanceList(@Body QueryServiceInstanceRequest request);

  @POST("/cluster/api/v1/component/query/dbList")
  Call<List<VirtualComponent>> queryDbList(@Body QueryComponentRequest request);

  @POST("/cluster/api/v1/component/query/cacheList")
  Call<List<VirtualComponent>> queryCacheList(@Body QueryComponentRequest request);

  @POST("/cluster/api/v1/component/query/mqList")
  Call<List<VirtualComponent>> queryMQList(@Body QueryComponentRequest request);

  @POST("/cluster/api/v1/component/query/componentTraceIds")
  Call<List<String>> queryComponentTraceIds(@Body QueryComponentRequest request);

  @POST("/cluster/api/v1/topology/query/tenantTopology")
  Call<Topology> queryTenantTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/topology/query/serviceTopology")
  Call<Topology> queryServiceTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/topology/query/serviceInstanceTopology")
  Call<Topology> queryServiceInstanceTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/topology/query/endpointTopology")
  Call<Topology> queryEndpointTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/topology/query/dbTopology")
  Call<Topology> queryDbTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/topology/query/mqTopology")
  Call<Topology> queryMQTopology(@Body QueryTopologyRequest request);

  @POST("/cluster/api/v1/slowSql/query")
  Call<List<SlowSql>> querySlowSqlList(@Body QueryComponentRequest request);

  @POST("/cluster/api/v1/service/query/serviceErrorList")
  Call<List<Map<String, String>>> queryServiceErrorList(@Body QueryServiceRequest request);

  @POST("/cluster/api/v1/service/query/serviceErrorDetail")
  Call<List<Map<String, String>>> queryServiceErrorDetail(@Body QueryServiceRequest request);

  @POST("/cluster/api/v1/event/query")
  Call<List<Event>> queryEvents(@Body Request request);
}
