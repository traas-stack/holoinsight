/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
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
import java.util.Map;

@RestController
@RequestMapping("/webapi/v1/trace")
@TokenUrls("/webapi/v1/trace")
@Slf4j
public class TraceQueryFacadeImpl extends BaseFacade {

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private TenantInitService tenantInitService;


  @PostMapping(value = "/query/basic")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<TraceBrief> queryBasicTraces(@RequestBody QueryTraceRequest request) {

    final JsonResult<TraceBrief> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceTags(ms.getTenant(), ms.getWorkspace(), request.getTags());
        if (!aBoolean) {
          throw new MonitorException("tags params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Trace> queryTrace(@RequestBody QueryTraceRequest request) {

    final JsonResult<Trace> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceTags(ms.getTenant(), ms.getWorkspace(), request.getTags());
        if (!aBoolean) {
          throw new MonitorException("tags params is illegal");
        }
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

  @PostMapping(value = "/query/traceTree")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<TraceTree>> queryTraceTree(@RequestBody QueryTraceRequest request) {

    final JsonResult<List<TraceTree>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceTags(ms.getTenant(), ms.getWorkspace(), request.getTags());
        if (!aBoolean) {
          throw new MonitorException("tags params is illegal");
        }
      }

      @Override
      public void doManage() {
        request.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<TraceTree> traceTreeList = queryClientService.queryTraceTree(request);
        JsonResult.createSuccessResult(result, traceTreeList);
      }
    });

    return result;
  }

  @PostMapping(value = "/query/serviceList")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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
   * 查询慢sql列表
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/slowSql")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
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

  /**
   * 查询异常列表
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/serviceErrorList")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, String>>> queryServiceErrorList(
      @RequestBody QueryProto.QueryMetaRequest request) {

    final JsonResult<List<Map<String, String>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<Map<String, String>> serviceErrorList =
            queryClientService.queryServiceErrorList(builder.build());
        JsonResult.createSuccessResult(result, serviceErrorList);
      }
    });

    return result;
  }

  /**
   * 查询异常详情
   *
   * @param request
   * @return
   */
  @PostMapping(value = "/serviceErrorDetail")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, String>>> queryServiceErrorDetail(
      @RequestBody QueryProto.QueryMetaRequest request) {
    final JsonResult<List<Map<String, String>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<Map<String, String>> serviceErrorDetail =
            queryClientService.queryServiceErrorDetail(builder.build());
        JsonResult.createSuccessResult(result, serviceErrorDetail);
      }
    });

    return result;
  }

  @PostMapping(value = "/events")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Event>> queryEvents(@RequestBody QueryProto.QueryEventRequest request) {
    final JsonResult<List<Event>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean = tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(),
            request.getTermParamsMap());
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryProto.QueryEventRequest.Builder builder = request.toBuilder();
        builder.setTenant(
            tenantInitService.getTraceTenant(RequestContext.getContext().ms.getTenant()));
        List<Event> events = queryClientService.queryEvents(builder.build());
        JsonResult.createSuccessResult(result, events);
      }
    });

    return result;
  }
}
