/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.DateUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.AlarmHistoryDetailService;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/6/10 11:08 上午
 */
@Slf4j
@RestController
@RequestMapping("/webapi/alarmHistoryDetail")
public class AlarmHistoryDetailFacadeImpl extends BaseFacade {

  @Autowired
  private AlarmHistoryDetailService alarmHistoryDetailService;


  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmHistoryDetailDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmHistoryDetailDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          pageRequest.getTarget().setWorkspace(ms.workspace);
        }
        JsonResult.createSuccessResult(result,
            alarmHistoryDetailService.getListByPage(pageRequest));
      }
    });

    return result;
  }

  @PostMapping("/countTrend")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<QueryResponse> queryCountTrend(
      @RequestBody MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest) {
    final JsonResult<QueryResponse> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
        ParaCheckUtil.checkParaNotNull(pageRequest.getFrom(), "from");
        ParaCheckUtil.checkParaNotNull(pageRequest.getTo(), "to");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          pageRequest.getTarget().setWorkspace(ms.workspace);
        }
        List<Map<String, Object>> countMap = alarmHistoryDetailService.count(pageRequest);
        QueryResponse response = new QueryResponse();
        convertQueryResponse(response, countMap, pageRequest.getFrom(), pageRequest.getTo(),
            pageRequest.getTarget());
        result.setData(response);
      }
    });
    return result;
  }

  private void convertQueryResponse(QueryResponse response, List<Map<String, Object>> countMap,
      Long from, Long to, AlarmHistoryDetailDTO target) {
    Map<Long, Double> values = new HashMap<>();
    if (!CollectionUtils.isEmpty(countMap)) {
      for (Map<String, Object> point : countMap) {
        try {
          Double value = ((Number) point.getOrDefault("c", 0d)).doubleValue();
          Object alarmTimeObj = point.get("alarm_time");
          Date alarmTime;
          if (alarmTimeObj instanceof String) {
            alarmTime = DateUtil.parseDate((String) alarmTimeObj, "yyyy-MM-dd HH:mm:ss");
          } else if (alarmTimeObj instanceof Date) {
            alarmTime = (Date) alarmTimeObj;
          } else {
            log.error("unknown case for alarmTimeObj {}", alarmTimeObj);
            continue;
          }
          values.put(alarmTime.getTime(), value);
        } catch (Exception e) {
          log.error("fail to convert countMap {}", J.toJson(countMap), e);
        }
      }
    }
    Result result = new Result();
    result.setValues(new ArrayList<>());
    long end = PeriodType.MINUTE.rounding(to);
    long start = PeriodType.MINUTE.rounding(from);
    for (long time = start; time <= end;) {
      Double v = values.getOrDefault(time, 0d);
      result.getValues().add(new Object[] {time, v});
      time += PeriodType.MINUTE.intervalMillis();
    }
    if (StringUtils.isNotBlank(target.getUniqueId())) {
      result.setTags(Collections.singletonMap("uniqueId", target.getUniqueId()));
    }
    result.setMetric("alert_history_detail_histogram");
    response.setResults(Collections.singletonList(result));
  }

}
