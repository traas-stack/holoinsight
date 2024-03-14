/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.*;
import io.holoinsight.server.home.dal.model.dto.IntegrationMetricDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationMetricsDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.query.grpc.QueryProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
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
public class IntegrationProductFacadeImpl extends BaseFacade {

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private IntegrationPluginService integrationPluginService;

  @Autowired
  private MetricInfoService metricInfoService;


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
        List<MetricInfoDTO> metricInfoDTOS = metricInfoService.queryListByTenantProduct(null, null,
            integrationProductDTO.getName().toLowerCase());
        if (!CollectionUtils.isEmpty(metricInfoDTOS)) {
          integrationProductDTO.setMetrics(convertIntegrationMetrics(metricInfoDTOS));
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
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("status", 1);
        columnMap.put("name", name);
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(columnMap);
        if (!CollectionUtils.isEmpty(integrationProductDTOs)) {
          integrationProductDTOs.forEach(integrationProductDTO -> {
            List<MetricInfoDTO> metricInfoDTOS = metricInfoService.queryListByTenantProduct(null,
                null, integrationProductDTO.getName().toLowerCase());
            if (CollectionUtils.isEmpty(metricInfoDTOS))
              return;
            integrationProductDTO.setMetrics(convertIntegrationMetrics(metricInfoDTOS));
          });
        }
        JsonResult.createSuccessResult(result, integrationProductDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/dataReceived")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Boolean>> dataReceived() {
    final JsonResult<Map<String, Boolean>> result = new JsonResult<>();
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("status", 1);
    columnMap.put("tenant", MonitorCookieUtil.getTenantOrException());
    List<IntegrationPluginDTO> integrationPluginDTOs =
        integrationPluginService.findByMap(columnMap);
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
                    .map(IntegrationMetricDTO::getName).collect(Collectors.toList());
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
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("status", 1);
        List<IntegrationProductDTO> integrationProductDTOs =
            integrationProductService.findByMap(columnMap);

        if (!CollectionUtils.isEmpty(integrationProductDTOs)) {
          integrationProductDTOs.forEach(integrationProductDTO -> {
            List<MetricInfoDTO> metricInfoDTOS = metricInfoService.queryListByTenantProduct(null,
                null, integrationProductDTO.getName().toLowerCase());
            if (CollectionUtils.isEmpty(metricInfoDTOS))
              return;
            integrationProductDTO.setMetrics(convertIntegrationMetrics(metricInfoDTOS));
          });
        }

        JsonResult.createSuccessResult(result, integrationProductDTOs);
      }
    });
    return result;
  }

  private IntegrationMetricsDTO convertIntegrationMetrics(List<MetricInfoDTO> metricInfoDTOS) {
    IntegrationMetricsDTO integrationMetricsDTO = new IntegrationMetricsDTO();
    Map<String, List<IntegrationMetricDTO>> subMetrics = new HashMap<>();
    metricInfoDTOS.forEach(metricsDTO -> {
      if (!subMetrics.containsKey(metricsDTO.getMetricType())) {
        subMetrics.put(metricsDTO.getMetricType(), new ArrayList<>());
      }
      subMetrics.get(metricsDTO.getMetricType())
          .add(new IntegrationMetricDTO(metricsDTO.getMetricTable(), metricsDTO.getMetric(),
              metricsDTO.getUnit(), metricsDTO.getDescription(), metricsDTO.getTags().toString(),
              metricsDTO.getPeriod().toString()));
    });
    integrationMetricsDTO.setSubMetrics(subMetrics);
    return integrationMetricsDTO;
  }
}
