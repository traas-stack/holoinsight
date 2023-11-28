/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AlertNotifyRecordService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author limengyang
 * @date 2023/7/17 17:08
 */
@Slf4j
@RestController
@RequestMapping("/webapi/alertNotifyRecord")
public class AlertNotifyRecordController extends BaseFacade {

  @Autowired
  private AlertNotifyRecordService alertNotifyRecordService;

  @GetMapping("/queryByHistoryDetailId/{historyDetailId}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlertNotifyRecordDTO> queryByHistoryDetailId(
      @PathVariable("historyDetailId") Long historyDetailId) {
    final JsonResult<AlertNotifyRecordDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(historyDetailId, "historyDetailId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlertNotifyRecordDTO save = alertNotifyRecordService.queryByHistoryDetailId(historyDetailId,
            ms.getTenant(), ms.getWorkspace());
        JsonResult.createSuccessResult(result, save);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlertNotifyRecordDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlertNotifyRecordDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlertNotifyRecordDTO>> result = new JsonResult<>();
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
        JsonResult.createSuccessResult(result, alertNotifyRecordService.getListByPage(pageRequest));
      }
    });

    return result;
  }
}
