/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.plugin.MetricInfoCheckService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.task.MetricCrawlerConstant;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoFacadeImpl.java, Date: 2023-04-24 Time: 20:42
 */
@RestController
@RequestMapping("/webapi/metricInfo")
public class MetricInfoFacadeImpl extends BaseFacade {

  @Autowired
  private MetricInfoService metricInfoService;

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private MetricInfoCheckService metricInfoCheckService;

  @GetMapping(value = "/query/products")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationProductDTO>> products() {
    final JsonResult<List<IntegrationProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result, integrationProductService.queryNames());
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByProduct/{product}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MetricInfoDTO>> queryByProduct(@PathVariable("product") String product) {
    final JsonResult<List<MetricInfoDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

        ParaCheckUtil.checkParaNotNull(product, "product");

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        JsonResult.createSuccessResult(result, metricInfoCheckService
            .queryMetricInfoByMetricType(ms.getTenant(), ms.getWorkspace(), product.toLowerCase()));
      }
    });
    return result;
  }

  @GetMapping(value = "/query/metrics")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MetricInfoDTO>> queryMetrics() {
    final JsonResult<List<MetricInfoDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {

        List<MetricInfoDTO> metricInfoDTOS = new ArrayList<>();
        MonitorScope ms = RequestContext.getContext().ms;
        List<MetricInfoDTO> globalMetrics = metricInfoService.queryListByTenant(
            MetricCrawlerConstant.GLOBAL_TENANT, MetricCrawlerConstant.GLOBAL_WORKSPACE);

        if (CollectionUtils.isEmpty(globalMetrics)) {
          metricInfoDTOS.addAll(globalMetrics);
        }

        List<MetricInfoDTO> tenantMetrics =
            metricInfoService.queryListByTenant(ms.getTenant(), ms.getWorkspace());

        if (CollectionUtils.isEmpty(tenantMetrics)) {
          metricInfoDTOS.addAll(tenantMetrics);
        }

        JsonResult.createSuccessResult(result, metricInfoDTOS);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryMetric/{metric}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MetricInfoDTO> queryMetric(@PathVariable("metric") String metric) {
    final JsonResult<MetricInfoDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(metric, "metric");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MetricInfoDTO metricInfoDTO =
            metricInfoService.queryByMetric(ms.getTenant(), ms.getWorkspace(), metric);
        JsonResult.createSuccessResult(result, metricInfoDTO);
      }
    });
    return result;
  }
}
