/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.task.MetricCrawlerConstant;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping(value = "/query/{tenant}/{workspace}/{product}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MetricInfoDTO>> queryByProduct(@PathVariable("tenant") String tenant,
      @PathVariable("workspace") String workspace, @PathVariable("product") String product) {
    final JsonResult<List<MetricInfoDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tenant, "tenant");
        ParaCheckUtil.checkParaNotNull(product, "product");
        MonitorScope ms = RequestContext.getContext().ms;
        if (!tenant.equalsIgnoreCase(MetricCrawlerConstant.GLOBAL_TENANT)
            && !ms.getTenant().equalsIgnoreCase(tenant)) {
          throw new MonitorException("tenant is illegal, " + tenant);
        }
        if (!tenant.equalsIgnoreCase(MetricCrawlerConstant.GLOBAL_WORKSPACE)
            && !ms.getWorkspace().equalsIgnoreCase(workspace)) {
          throw new MonitorException("tenant is illegal, " + tenant);
        }
      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result,
            metricInfoService.queryListByTenantProduct(tenant, workspace, product));
      }
    });
    return result;
  }
}
