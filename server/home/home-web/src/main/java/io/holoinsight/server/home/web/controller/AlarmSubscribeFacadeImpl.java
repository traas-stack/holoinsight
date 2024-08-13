/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.scope.IdentityType;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.common.MonitorException;
import io.holoinsight.server.common.ResultCodeEnum;
import io.holoinsight.server.common.dao.entity.AlarmSubscribe;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeInfo;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import lombok.extern.slf4j.Slf4j;
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
import io.holoinsight.server.common.service.AlertSubscribeService;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeDTO;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wangsiyuan
 * @date 2022/4/18 11:02 下午
 */
@RestController
@Slf4j
@RequestMapping("/webapi/alarmSubscribe")
public class AlarmSubscribeFacadeImpl extends BaseFacade {

  @Autowired
  private AlertSubscribeService alarmSubscribeService;

  @Autowired
  private ULAFacade ulaFacade;

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  @Autowired
  private ParameterSecurityService parameterSecurityService;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!uniqueId"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmSubscribeFacadeImplChecker")
  @GetMapping(value = "/queryByUniqueId/{uniqueId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmSubscribeDTO> queryByUniqueId(@PathVariable("uniqueId") String uniqueId) {
    final JsonResult<AlarmSubscribeDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryWrapper<AlarmSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unique_id", uniqueId);
        requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, ms.getTenant(),
            ms.getWorkspace());
        JsonResult.createSuccessResult(result,
            alarmSubscribeService.queryByUniqueId(queryWrapper, uniqueId));
      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmSubscribeDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmSubscribeFacadeImplChecker")
  @PostMapping("/submit")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> submit(@RequestBody AlarmSubscribeDTO alarmSubscribeDTO) {
    JsonResult<Boolean> jsonResult = new JsonResult<>();
    Boolean aBoolean = checkMembers(alarmSubscribeDTO);
    if (!aBoolean) {
      JsonResult.fillFailResultTo(jsonResult, jsonResult.getMessage());
      return jsonResult;
    }

    return saveBatch(alarmSubscribeDTO);
  }

  public JsonResult<Boolean> saveBatch(AlarmSubscribeDTO alarmSubscribeDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorUser mu = RequestContext.getContext().mu;
        String creator = null;
        if (null != mu) {
          creator = mu.getLoginName();
        }
        boolean rtn =
            alarmSubscribeService.saveDataBatch(alarmSubscribeDTO, creator, tenant(), workspace());
        JsonResult.createSuccessResult(result, rtn);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!uniqueId"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmSubscribeFacadeImplChecker")
  @GetMapping(value = "/querySubUsers/{uniqueId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MonitorUser>> querySubUsers(@PathVariable("uniqueId") String uniqueId) {
    final JsonResult<List<MonitorUser>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        QueryWrapper<AlarmSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unique_id", uniqueId);
        requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant(), workspace());
        AlarmSubscribeDTO alarmSubscribeDTO =
            alarmSubscribeService.queryByUniqueId(queryWrapper, uniqueId);

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

  private Boolean checkMembers(AlarmSubscribeDTO alarmSubscribeDTO) {
    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;
    if (mu.getIdentityType() == IdentityType.OUTTOKEN) {
      log.warn("skip members check process for {}.", J.toJson(mu));
      return true;
    }
    if (null == alarmSubscribeDTO || CollectionUtils.isEmpty(alarmSubscribeDTO.getAlarmSubscribe()))
      return true;
    List<AlarmSubscribeInfo> alarmSubscribes = alarmSubscribeDTO.getAlarmSubscribe();
    Set<String> userIds = ulaFacade.getCurrentULA().getUserIds(mu, ms);

    for (AlarmSubscribeInfo alarmSubscribeInfo : alarmSubscribes) {
      if (CollectionUtils.isEmpty(alarmSubscribeInfo.getNoticeType())
          || StringUtils.isBlank(alarmSubscribeInfo.getSubscriber())) {
        continue;
      }

      if (alarmSubscribeInfo.getNoticeType().contains("sms")
          || alarmSubscribeInfo.getNoticeType().contains("phone")
          || alarmSubscribeInfo.getNoticeType().contains("email")) {
        if (!userIds.contains(alarmSubscribeInfo.getSubscriber())) {
          return false;
        }
      }
    }

    return true;
  }
}
