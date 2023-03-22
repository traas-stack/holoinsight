/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AlertSubscribeService;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;

/**
 * @author wangsiyuan
 * @date 2022/4/18 11:02 下午
 */
@RestController
@RequestMapping("/webapi/alarmSubscribe")
public class AlarmSubscribeFacadeImpl extends BaseFacade {

  @Autowired
  private AlertSubscribeService alarmSubscribeService;

  @Autowired
  private ULAFacade ulaFacade;

  @GetMapping(value = "/queryByUniqueId/{uniqueId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmSubscribeDTO> queryByUniqueId(@PathVariable("uniqueId") String uniqueId) {
    final JsonResult<AlarmSubscribeDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(uniqueId, "uniqueId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("unique_id", uniqueId);
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          conditions.put("workspace", ms.getWorkspace());
        }
        JsonResult.createSuccessResult(result, alarmSubscribeService.queryByUniqueId(conditions));
      }
    });
    return result;
  }

  @PostMapping("/submit")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> saveBatch(@RequestBody AlarmSubscribeDTO alarmSubscribeDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmSubscribeDTO, "alarmSubscribeDTO");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        String creator = null;
        String tenant = null;
        if (null != mu) {
          creator = mu.getLoginName();
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          tenant = ms.tenant;
        }
        boolean rtn =
            alarmSubscribeService.saveDataBatch(alarmSubscribeDTO, creator, tenant, ms.workspace);
        JsonResult.createSuccessResult(result, rtn);
      }
    });

    return result;
  }

  @GetMapping(value = "/querySubUsers/{uniqueId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MonitorUser>> querySubUsers(@PathVariable("uniqueId") String uniqueId) {
    final JsonResult<List<MonitorUser>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(uniqueId, "uniqueId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("unique_id", uniqueId);
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          conditions.put("workspace", ms.getWorkspace());
        }
        AlarmSubscribeDTO alarmSubscribeDTO = alarmSubscribeService.queryByUniqueId(conditions);

        if (null == alarmSubscribeDTO
            || CollectionUtils.isEmpty(alarmSubscribeDTO.getAlarmSubscribe()))
          return;

        List<MonitorUser> monitorUsers = new ArrayList<>();

        alarmSubscribeDTO.getAlarmSubscribe().forEach(alarmSubscribeInfo -> {
          if (CollectionUtils.isEmpty(alarmSubscribeInfo.getNoticeType()))
            return;

          if (alarmSubscribeInfo.getNoticeType().contains("dingding")
              || alarmSubscribeInfo.getNoticeType().contains("sms")
              || alarmSubscribeInfo.getNoticeType().contains("phone")
              || alarmSubscribeInfo.getNoticeType().contains("email")) {
            MonitorUser byLoginName =
                ulaFacade.getCurrentULA().getByUserId(alarmSubscribeInfo.getSubscriber());
            if (null != byLoginName) {
              monitorUsers.add(byLoginName);
            }
          }
        });

        JsonResult.createSuccessResult(result, monitorUsers);

      }
    });
    return result;
  }
}
