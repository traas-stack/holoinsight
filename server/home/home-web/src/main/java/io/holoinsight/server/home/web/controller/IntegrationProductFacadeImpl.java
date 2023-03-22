/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.*;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.IntegrationMetricDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationMetricsDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.query.grpc.QueryProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/integration/product")
@TokenUrls("/webapi/integration/product/create")
public class IntegrationProductFacadeImpl extends BaseFacade {

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private IntegrationPluginService integrationPluginService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody IntegrationProductDTO integrationProductDTO) {
    final JsonResult<IntegrationProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.id, "id");
        ParaCheckUtil.checkParaNotBlank(integrationProductDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.overview, "overview");
        ParaCheckUtil.checkParaNotBlank(integrationProductDTO.configuration, "configuration");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.metrics, "metrics");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.status, "status");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          integrationProductDTO.setModifier(mu.getLoginName());
        }
        integrationProductDTO.setGmtModified(new Date());
        IntegrationProductDTO update =
            integrationProductService.updateByRequest(integrationProductDTO);

        assert mu != null;
        userOpLogService.append("integration_product", update.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(integrationProductDTO),
            J.toJson(update), null, "integration_product_update");

      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<IntegrationProductDTO> save(
      @RequestBody IntegrationProductDTO integrationProductDTO) {
    final JsonResult<IntegrationProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(integrationProductDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.overview, "overview");
        ParaCheckUtil.checkParaNotBlank(integrationProductDTO.configuration, "configuration");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.metrics, "metrics");
        ParaCheckUtil.checkParaNotNull(integrationProductDTO.status, "status");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          integrationProductDTO.setCreator(mu.getLoginName());
          integrationProductDTO.setModifier(mu.getLoginName());
        }
        IntegrationProductDTO save = integrationProductService.create(integrationProductDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("integration_product", save.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(integrationProductDTO),
            null, null, "integration_product_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/queryById/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<IntegrationProductDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<IntegrationProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        IntegrationProductDTO integrationProductDTO = integrationProductService.findById(id);

        if (null == integrationProductDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, integrationProductDTO);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationProductDTO>> queryByName(@PathVariable("name") String name) {
    final JsonResult<List<IntegrationProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(Collections.singletonMap("name", name));
        JsonResult.createSuccessResult(result, integrationProductDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/dataReceived/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Boolean> dataReceivedByName(@PathVariable("name") String name) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(Collections.singletonMap("name", name));
        if (!CollectionUtils.isEmpty(integrationProductDTOs)) {
          IntegrationProductDTO integrationProductDTO = integrationProductDTOs.get(0);
          IntegrationMetricsDTO metrics = integrationProductDTO.getMetrics();
          String tenant = MonitorCookieUtil.getTenantOrException();
          long now = System.currentTimeMillis() / 60000 * 60000;
          long from = now - 60000 * 60;
          QueryProto.QueryRequest.Builder builder =
              QueryProto.QueryRequest.newBuilder().setTenant(tenant);
          for (String type : metrics.getSubMetrics().keySet()) {
            for (IntegrationMetricDTO metric : metrics.getSubMetrics().get(type)) {
              builder.addDatasources(QueryProto.Datasource.newBuilder().setMetric(metric.getName())
                  .setStart(from).setEnd(now).build());
            }
          }
          QueryResponse response = queryClientService.queryTags(builder.build());
          List<Result> results = response.getResults();
          if (results != null && results.stream().anyMatch(result -> result.getTags() != null)) {
            JsonResult.createSuccessResult(result, true);
          } else {
            result.setData(false);
          }
        }
      }
    });
    return result;
  }

  @GetMapping(value = "/dataReceived")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Boolean>> dataReceived() {
    final JsonResult<Map<String, Boolean>> result = new JsonResult<>();
    List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService
        .findByMap(Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        Map<String, Boolean> nameResults = new HashMap<>();
        JsonResult.createSuccessResult(result, nameResults);
        String tenant = MonitorCookieUtil.getTenantOrException();
        long now = System.currentTimeMillis() / 60000 * 60000;
        long from = now - 60000 * 60;
        integrationPluginDTOs.parallelStream().forEach(plugin -> {
          String productName = plugin.getProduct();
          List<IntegrationProductDTO> integrationProductDTOs =
              integrationProductService.findByMap(Collections.singletonMap("name", productName));
          if (!CollectionUtils.isEmpty(integrationProductDTOs)) {
            IntegrationProductDTO integrationProductDTO = integrationProductDTOs.get(0);
            IntegrationMetricsDTO metrics = integrationProductDTO.getMetrics();
            if (CollectionUtils.isEmpty(metrics.getSubMetrics())
                || CollectionUtils.isEmpty(metrics.getSubMetrics().values())) {
              nameResults.put(productName, true);
              return;
            }
            List<String> metricNames =
                metrics.getSubMetrics().values().stream().flatMap(v -> v.stream())
                    .map(IntegrationMetricDTO::getName).collect(Collectors.toList());;
            Collections.shuffle(metricNames);
            List<String> sampleNames = metricNames.subList(0, Math.min(metricNames.size(), 10));
            QueryProto.QueryRequest.Builder builder =
                QueryProto.QueryRequest.newBuilder().setTenant(tenant);
            for (String metricName : sampleNames) {
              builder.addDatasources(QueryProto.Datasource.newBuilder().setMetric(metricName)
                  .setStart(from).setEnd(now).build());
            }
            QueryResponse response = queryClientService.queryTags(builder.build());
            List<Result> results = response.getResults();
            if (results != null && results.stream().anyMatch(result -> result.getTags() != null)) {
              nameResults.put(productName, true);
            } else {
              nameResults.put(productName, false);
            }
          }
        });
      }
    });
    return result;
  }

  @GetMapping(value = "/listAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationProductDTO>> listAll() {
    final JsonResult<List<IntegrationProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(Collections.emptyMap());

        JsonResult.createSuccessResult(result, integrationProductDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAllNames")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> listAllNames() {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(Collections.emptyMap());
        List<String> names = integrationProductDTOs == null ? null
            : integrationProductDTOs.stream().map(IntegrationProductDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
      }
    });
    return result;
  }

  // @DeleteMapping(value = "/delete/{id}")
  // @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  // public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
  // final JsonResult<Object> result = new JsonResult<>();
  // facadeTemplate.manage(result, new ManageCallback() {
  // @Override
  // public void checkParameter() {
  // ParaCheckUtil.checkParaNotNull(id, "id");
  // }
  //
  // @Override
  // public void doManage() {
  // IntegrationProductDTO byId = integrationProductService.findById(id);
  // integrationProductService.deleteById(id);
  // JsonResult.createSuccessResult(result, null);
  // userOpLogService.append("integration_product", String.valueOf(byId.getId()),
  // OpType.DELETE, RequestContext.getContext().mu.getLoginName(),
  // RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
  // "integration_product_create");
  //
  // }
  // });
  // return result;
  // }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<IntegrationProductDTO>> pageQuery(
      @RequestBody MonitorPageRequest<IntegrationProductDTO> customPluginRequest) {
    final JsonResult<MonitorPageResult<IntegrationProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result,
            integrationProductService.getListByPage(customPluginRequest));
      }
    });

    return result;
  }
}
