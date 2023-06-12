/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AlarmMetricService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AlarmMetric;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.apache.commons.lang3.StringUtils;
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
 * @version 1.0: AlarmMetricFacadeImpl.java, Date: 2023-06-08 Time: 21:56
 */
@RestController
@RequestMapping("/webapi/alarmMetric/query")
public class AlarmMetricFacadeImpl extends BaseFacade {

  @Autowired
  private AlarmMetricService alarmMetricService;

  @GetMapping(value = "/{metric}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<AlarmMetric>> queryMetric(@PathVariable("metric") String metric) {
    final JsonResult<List<AlarmMetric>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(metric, "metric");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        List<AlarmMetric> alarmMetrics = new ArrayList<>();
        String[] split = StringUtils.split(metric, ",");
        for (String m : split) {
          List<AlarmMetric> metrics =
              alarmMetricService.queryByMetric(m, ms.getTenant(), ms.getWorkspace());
          if (!CollectionUtils.isEmpty(metrics)) {
            alarmMetrics.addAll(metrics);
          }
        }

        JsonResult.createSuccessResult(result, alarmMetrics);
      }
    });
    return result;
  }
}
