/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.AgentConfigurationService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.service.query.KeyResult;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.QuerySchemaResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.service.query.ValueResult;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AgentConfiguration;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.PqlParser;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.common.pql.PqlException;
import io.holoinsight.server.home.web.controller.model.DataQueryRequest;
import io.holoinsight.server.home.web.controller.model.PqlInstanceRequest;
import io.holoinsight.server.home.web.controller.model.PqlParseRequest;
import io.holoinsight.server.home.web.controller.model.PqlParseResult;
import io.holoinsight.server.home.web.controller.model.PqlRangeQueryRequest;
import io.holoinsight.server.home.web.controller.model.TagQueryRequest;
import io.holoinsight.server.home.web.controller.model.open.GrafanaJsonResult;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/webapi/v1/query")
@TokenUrls("/webapi/v1/query")
public class QueryFacadeImpl extends BaseFacade {

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private AgentConfigurationService agentConfigurationService;

  @Autowired
  private PqlParser pqlParser;

  @PostMapping
  public JsonResult<QueryResponse> query(@RequestBody DataQueryRequest request) {

    final JsonResult<QueryResponse> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotEmpty(request.datasources, "datasources");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryResponse response = queryClientService.query(converRequest(request));
        JsonResult.createSuccessResult(result, response);
      }
    });

    return result;
  }

  @PostMapping("/tags")
  public JsonResult<?> queryTags(@RequestBody DataQueryRequest request) {

    final JsonResult<QueryResponse> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotEmpty(request.datasources, "datasources");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryResponse response = queryClientService.queryTags(converRequest(request));

        JsonResult.createSuccessResult(result, response);
      }
    });

    return result;
  }

  @PostMapping("/deltags")
  public JsonResult<?> deltags(@RequestBody DataQueryRequest request) {

    final JsonResult<Boolean> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotEmpty(request.datasources, "datasources");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        queryClientService.delTags(converRequest(request));

        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

  @GetMapping("/schema")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<KeyResult> tagsKey(@RequestParam("name") String metric) {
    final JsonResult<KeyResult> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryProto.Datasource datasource = QueryProto.Datasource.newBuilder().setMetric(metric)
            .setStart(System.currentTimeMillis() - 60000 * 60 * 5)
            .setEnd(System.currentTimeMillis() - 60000 * 5).build();
        QueryProto.QueryRequest.Builder builder = QueryProto.QueryRequest.newBuilder();
        if (null != ms) {
          builder.setTenant(ms.getTenant());
        }

        QueryProto.QueryRequest request =
            builder.addAllDatasources(Collections.singletonList(datasource)).build();
        QuerySchemaResponse querySchema = queryClientService.querySchema(request);
        KeyResult keyResult = new KeyResult();
        if (querySchema != null && querySchema.getResults() != null
            && !querySchema.getResults().isEmpty()) {
          keyResult = querySchema.getResults().get(0);
        }
        JsonResult.createSuccessResult(result, keyResult);
      }
    });

    return result;
  }

  /**
   * 模糊匹配查询
   *
   * @param name
   * @return
   */
  @GetMapping("/metric")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<List<String>> metric(@RequestParam("name") String name) {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryProto.QueryMetricsRequest.Builder builder =
            QueryProto.QueryMetricsRequest.newBuilder();
        if (null != ms) {
          builder.setTenant(ms.getTenant());
        }
        builder.setName(name);
        builder.setLimit(3000);
        QueryProto.QueryMetricsRequest queryMetricsRequest = builder.build();
        List<String> list = queryClientService.queryMetrics(queryMetricsRequest);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  /**
   * 查询指定tag的值
   *
   * @param tagQueryRequest
   * @return
   */
  @PostMapping("/tagValues")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<ValueResult> queryTagValues(@RequestBody TagQueryRequest tagQueryRequest) {

    final JsonResult<ValueResult> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tagQueryRequest, "request");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryProto.Datasource datasource = QueryProto.Datasource.newBuilder()
            .setMetric(tagQueryRequest.getMetric()).setStart(System.currentTimeMillis() - 60000 * 5)
            .setEnd(System.currentTimeMillis() - 60000 * 4).setAggregator("count")
            .addAllGroupBy(Collections.singletonList(tagQueryRequest.getKey())).build();

        QueryProto.QueryRequest.Builder builder = QueryProto.QueryRequest.newBuilder()
            .addAllDatasources(Collections.singletonList(datasource));
        if (null != ms) {
          builder.setTenant(ms.getTenant());
        }
        ValueResult response =
            queryClientService.queryTagValues(builder.build(), tagQueryRequest.getKey());
        JsonResult.createSuccessResult(result, response);
      }
    });

    return result;
  }

  @PostMapping(value = "/pql/range")
  public GrafanaJsonResult<List<Result>> pqlRangeQuery(@RequestBody PqlRangeQueryRequest request) {

    final GrafanaJsonResult<List<Result>> result = new GrafanaJsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryProto.PqlRangeRequest rangeRequest = QueryProto.PqlRangeRequest.newBuilder()
            .setQuery(request.getQuery()).setTenant(RequestContext.getContext().ms.getTenant())
            .setDelta(request.getDelta()).setTimeout(request.getTimeout())
            .setStart(request.getStart()).setEnd(request.getEnd())
            .setFillZero(request.getFillZero()).setStep(request.getStep()).build();
        QueryResponse response = queryClientService.pqlRangeQuery(rangeRequest);
        GrafanaJsonResult.createSuccessResult(result, response.getResults());
      }
    });

    return result;
  }

  @PostMapping(value = "/pql/instant")
  public GrafanaJsonResult<List<Result>> pqlInstanceQuery(@RequestBody PqlInstanceRequest request) {
    final GrafanaJsonResult<List<Result>> result = new GrafanaJsonResult<>();
    RequestContext.Context ctx = RequestContext.getContext();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryProto.PqlInstantRequest instantRequest =
            QueryProto.PqlInstantRequest.newBuilder().setQuery(request.getQuery())
                .setTenant(RequestContext.getContext().ms.getTenant()).setDelta(request.getDelta())
                .setTimeout(request.getTimeout()).setTime(request.getTime()).build();
        QueryResponse response = queryClientService.pqlInstantQuery(instantRequest);
        GrafanaJsonResult.createSuccessResult(result, response.getResults());
      }
    });

    return result;
  }

  @PostMapping(value = "/trace/query/basic")
  public JsonResult<TraceBrief> queryBasicTraces(@RequestBody QueryTraceRequest request) {

    final JsonResult<TraceBrief> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        request.setTenant(RequestContext.getContext().ms.getTenant());
        TraceBrief traceBrief = queryClientService.queryBasicTraces(request);
        JsonResult.createSuccessResult(result, traceBrief);
      }
    });

    return result;
  }

  @PostMapping(value = "/trace/query")
  public JsonResult<Trace> queryTrace(@RequestBody QueryTraceRequest request) {

    final JsonResult<Trace> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        request.setTenant(RequestContext.getContext().ms.getTenant());
        Trace trace = queryClientService.queryTrace(request);
        JsonResult.createSuccessResult(result, trace);
      }
    });

    return result;
  }

  @PostMapping(value = "/service/query/serviceList")
  public JsonResult<List<Service>> queryServiceList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<Service>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<Service> services = queryClientService.queryServiceList(request);
        // search by serviceName
        if (!StringUtils.isEmpty(request.getServiceName())) {
          Iterator<Service> iterator = services.iterator();
          while (iterator.hasNext()) {
            String name = iterator.next().getName();
            if (!name.equals(request.getServiceName())
                && !name.contains(request.getServiceName())) {
              iterator.remove();
            }
          }
        }
        JsonResult.createSuccessResult(result, services);
      }
    });

    return result;
  }

  @PostMapping(value = "/endpoint/query/endpointList")
  public JsonResult<List<Endpoint>> queryEndpointList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<Endpoint>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(request.getServiceName(), "serviceName");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<Endpoint> endpoints = queryClientService.queryEndpointList(request);
        JsonResult.createSuccessResult(result, endpoints);
      }
    });

    return result;
  }

  @PostMapping(value = "/serviceInstance/query/serviceInstanceList")
  public JsonResult<List<ServiceInstance>> queryServiceInstanceList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<ServiceInstance>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(request.getServiceName(), "serviceName");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<ServiceInstance> serviceInstances =
            queryClientService.queryServiceInstanceList(request);
        JsonResult.createSuccessResult(result, serviceInstances);
      }
    });

    return result;
  }

  /**
   * 查询组件列表
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/component/query/componentList")
  public JsonResult<List<VirtualComponent>> queryComponentList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<VirtualComponent>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(request.getServiceName(), "serviceName");
        ParaCheckUtil.checkParaNotNull(request.getCategory(), "category");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<VirtualComponent> VirtualComponents = queryClientService.queryComponentList(request);
        JsonResult.createSuccessResult(result, VirtualComponents);
      }
    });

    return result;
  }

  @PostMapping(value = "/component/query/componentTraceIds")
  public JsonResult<List<String>> queryComponentTraceIds(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<String>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(request.getServiceName(), "serviceName");
        ParaCheckUtil.checkParaNotNull(request.getAddress(), "address");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<String> traceIds = queryClientService.queryComponentTraceIds(request);
        JsonResult.createSuccessResult(result, traceIds);
      }
    });

    return result;
  }

  /**
   * 查询拓扑
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/topology/query/topology")
  public JsonResult<Topology> queryTenantTopology(
      @RequestBody QueryProto.QueryTopologyRequest request) {

    final JsonResult<Topology> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(request.getCategory(), "category");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        Topology topology = queryClientService.queryTopology(request);
        JsonResult.createSuccessResult(result, topology);
      }
    });

    return result;
  }

  /**
   * agent 配置下发
   *
   * @return
   */
  @PostMapping(value = "/agent/create/configuration")
  public JsonResult<Boolean> createAgentConfiguration(
      @RequestBody AgentConfiguration agentConfiguration) {

    final JsonResult<Boolean> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(agentConfiguration, "request");
        ParaCheckUtil.checkParaNotNull(agentConfiguration.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(agentConfiguration.getService(), "service");
        ParaCheckUtil.checkEquals(agentConfiguration.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");
      }

      @Override
      public void doManage() {
        if (StringUtils.isEmpty(agentConfiguration.getAppId())) {
          agentConfiguration.setAppId("*");
        }
        if (StringUtils.isEmpty(agentConfiguration.getEnvId())) {
          agentConfiguration.setEnvId("*");
        }
        boolean isSuccess = agentConfigurationService.createOrUpdate(agentConfiguration);
        if (isSuccess) {
          JsonResult.createSuccessResult(result, isSuccess);
        } else {
          JsonResult.createFailResult(result, "Create agent configuration failed!");
        }
      }
    });

    return result;
  }

  /**
   * agent 配置获取
   *
   * @return
   */
  @PostMapping(value = "/agent/query/configuration")
  public JsonResult<AgentConfiguration> queryAgentConfiguration(
      @RequestBody AgentConfiguration agentConfiguration) {

    final JsonResult<AgentConfiguration> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(agentConfiguration, "request");
        ParaCheckUtil.checkParaNotNull(agentConfiguration.getTenant(), "tenant");
        ParaCheckUtil.checkParaNotNull(agentConfiguration.getService(), "service");
        ParaCheckUtil.checkEquals(agentConfiguration.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");
      }

      @Override
      public void doManage() {
        if (StringUtils.isEmpty(agentConfiguration.getAppId())) {
          agentConfiguration.setAppId("*");
        }
        if (StringUtils.isEmpty(agentConfiguration.getEnvId())) {
          agentConfiguration.setEnvId("*");
        }
        AgentConfiguration configuration = agentConfigurationService.get(agentConfiguration);
        JsonResult.createSuccessResult(result, configuration);
      }
    });

    return result;
  }


  /**
   * 查询慢sql列表
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/slowSql")
  public JsonResult<List<SlowSql>> querySlowSqlList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<SlowSql>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        List<SlowSql> slowSqlList = queryClientService.querySlowSqlList(request);
        JsonResult.createSuccessResult(result, slowSqlList);
      }
    });

    return result;
  }

  public QueryProto.QueryRequest converRequest(DataQueryRequest request) {
    MonitorScope ms = RequestContext.getContext().ms;
    QueryProto.QueryRequest.Builder builder = QueryProto.QueryRequest.newBuilder();

    if (StringUtil.isNotBlank(request.getTenant())) {
      builder.setTenant(request.getTenant());
    } else if (null != ms && StringUtil.isNotBlank(ms.getTenant())) {
      builder.setTenant(ms.getTenant());
    }

    if (StringUtil.isNotBlank(request.getQuery())) {
      builder.setQuery(request.getQuery());
    }

    request.datasources.forEach(d -> {
      QueryProto.Datasource.Builder datasourceBuilder = QueryProto.Datasource.newBuilder();
      toProtoBean(datasourceBuilder, d);
      datasourceBuilder
          .setApmMaterialized(d.isApmMaterialized() || MetaDictUtil.isApmMaterialized());
      builder.addDatasources(datasourceBuilder);
    });

    return builder.build();
  }

  public static void toProtoBean(Message.Builder destPojoClass, Object source) {
    String json = J.toJson(source);
    try {
      JsonFormat.parser().merge(json, destPojoClass);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("InvalidProtocolBufferException error", e);
    }
  }

  @PostMapping(value = "/pql/parse")
  public JsonResult<PqlParseResult> pqlParse(@RequestBody PqlParseRequest request) {
    final JsonResult<PqlParseResult> result = new JsonResult<>();
    PqlParseResult pqlParseResult = new PqlParseResult();
    pqlParseResult.setRawPql(request.getPql());
    try {
      List<String> exprs = pqlParser.parseList(request.getPql());
      if (exprs != null && !exprs.isEmpty()) {
        pqlParseResult.setExprs(exprs);
        JsonResult.createSuccessResult(result, pqlParseResult);
      } else {
        JsonResult.createFailResult(result, "parse failed or pql is empty");
      }
    } catch (PqlException e) {
      JsonResult.createFailResult(result, e.getMessage());
    }
    return result;
  }
}
