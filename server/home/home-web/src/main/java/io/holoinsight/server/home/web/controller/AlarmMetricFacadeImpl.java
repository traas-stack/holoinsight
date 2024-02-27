/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: AlarmMetricFacadeImpl.java, Date: 2023-06-08 Time: 21:56
 */
@RestController
@RequestMapping("/webapi/alarmMetric/query")
public class AlarmMetricFacadeImpl extends BaseFacade {

  @Autowired
  private AlarmMetricService alarmMetricService;

  @Autowired
  private MetricInfoService metricInfoService;

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

  @GetMapping(value = "/relate/{ruleId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, List<String>>> queryMetricByRuleId(
      @PathVariable("ruleId") Long ruleId) {
    final JsonResult<Map<String, List<String>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(ruleId, "ruleId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, List<String>> refMaps = new HashMap<>();

        List<AlarmMetric> metrics =
            alarmMetricService.queryByRuleId(ruleId, ms.getTenant(), ms.getWorkspace());
        if (CollectionUtils.isEmpty(metrics)) {
          JsonResult.createSuccessResult(result, refMaps);
          return;
        }

        for (AlarmMetric alarmMetric : metrics) {
          MetricInfoDTO metricInfoDTO = metricInfoService.queryByMetric(alarmMetric.getTenant(),
              alarmMetric.getWorkspace(), alarmMetric.getMetricTable());
          if (null == metricInfoDTO || StringUtils.isBlank(metricInfoDTO.getRef()))
            continue;
          if (!refMaps.containsKey(metricInfoDTO.getProduct())) {
            refMaps.put(metricInfoDTO.getProduct(), new ArrayList<>());
          }
          refMaps.get(metricInfoDTO.getProduct()).add(metricInfoDTO.getRef());
        }

        JsonResult.createSuccessResult(result, refMaps);
      }
    });
    return result;
  }
}
