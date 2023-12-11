/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
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
import io.holoinsight.server.home.biz.service.AlertSubscribeService;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
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
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmSubscribeDTO, "alarmSubscribeDTO");
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (StringUtils.isNotEmpty(alarmSubscribeDTO.getUniqueId())) {
          ParaCheckUtil.checkParaBoolean(
              parameterSecurityService.checkRuleTenantAndWorkspace(alarmSubscribeDTO.getUniqueId(),
                  ms.getTenant(), ms.getWorkspace()),
              "uniqueId do not belong to this tenant or workspace");
        }
        if (!CollectionUtils.isEmpty(alarmSubscribeDTO.getAlarmSubscribe())) {
          for (AlarmSubscribeInfo alarmSubscribeInfo : alarmSubscribeDTO.getAlarmSubscribe()) {
            if (StringUtils.isNotEmpty(alarmSubscribeInfo.getUniqueId())) {
              ParaCheckUtil.checkParaBoolean(
                  parameterSecurityService.checkRuleTenantAndWorkspace(
                      alarmSubscribeInfo.getUniqueId(), ms.getTenant(), ms.getWorkspace()),
                  "uniqueId do not belong to this tenant or workspace");
            }
            if (CollectionUtils.isEmpty(alarmSubscribeInfo.getNoticeType())) {
              continue;
            }
            if (alarmSubscribeInfo.getNoticeType().contains("dingding")
                || alarmSubscribeInfo.getNoticeType().contains("sms")
                || alarmSubscribeInfo.getNoticeType().contains("phone")
                || alarmSubscribeInfo.getNoticeType().contains("email")) {
              ParaCheckUtil.checkParaBoolean(parameterSecurityService.checkUserTenantAndWorkspace(
                  alarmSubscribeInfo.getSubscriber(), mu), "invalid subscriber");
            }
            if (alarmSubscribeInfo.getNoticeType().contains("dingDingRobot")) {
              ParaCheckUtil.checkParaBoolean(parameterSecurityService.checkGroupTenantAndWorkspace(
                  alarmSubscribeInfo.getGroupId(), ms.getTenant(),
                  requestContextAdapter.getWorkspace(true)), "invalid subscriber");
            }

            if (null != alarmSubscribeInfo.getId()) {
              AlarmSubscribe byId = alarmSubscribeService.getById(alarmSubscribeInfo.getId());
              if (null == byId) {
                throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "id not existed");
              }
            }
          }
        }
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
        QueryWrapper<AlarmSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unique_id", uniqueId);
        requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, ms.getTenant(),
            ms.getWorkspace());
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
