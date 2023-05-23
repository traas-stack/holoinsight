/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AgentConfigurationService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AgentConfiguration;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.Builder;
import io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/webapi/v1/trace")
@TokenUrls("/webapi/v1/trace")
@Slf4j
public class TraceQueryFacadeImpl extends BaseFacade {

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private AgentConfigurationService agentConfigurationService;

  @Autowired
  private TenantInitService tenantInitService;


  @PostMapping(value = "/query/basic")
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
        request.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        TraceBrief traceBrief = queryClientService.queryBasicTraces(request);
        JsonResult.createSuccessResult(result, traceBrief);
      }
    });

    return result;
  }

  @PostMapping(value = "/query")
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
        request.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        Trace trace = queryClientService.queryTrace(request);
        JsonResult.createSuccessResult(result, trace);
      }
    });

    return result;
  }

  @PostMapping(value = "/query/serviceList")
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<Service> services = queryClientService.queryServiceList(builder.build());
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

  @PostMapping(value = "/query/endpointList")
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<Endpoint> endpoints = queryClientService.queryEndpointList(builder.build());
        JsonResult.createSuccessResult(result, endpoints);
      }
    });

    return result;
  }

  @PostMapping(value = "/query/serviceInstanceList")
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<ServiceInstance> serviceInstances =
            queryClientService.queryServiceInstanceList(builder.build());
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
  @PostMapping(value = "/query/componentList")
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<VirtualComponent> VirtualComponents =
            queryClientService.queryComponentList(builder.build());
        JsonResult.createSuccessResult(result, VirtualComponents);
      }
    });

    return result;
  }

  @PostMapping(value = "/query/componentTraceIds")
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<String> traceIds = queryClientService.queryComponentTraceIds(builder.build());
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
  @PostMapping(value = "/query/topology")
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
        QueryTopologyRequest.Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        Topology topology = queryClientService.queryTopology(builder.build());
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
          JsonResult.fillFailResultTo(result, "Create agent configuration failed!");
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
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<SlowSql> slowSqlList = queryClientService.querySlowSqlList(builder.build());
        JsonResult.createSuccessResult(result, slowSqlList);
      }
    });

    return result;
  }
}
