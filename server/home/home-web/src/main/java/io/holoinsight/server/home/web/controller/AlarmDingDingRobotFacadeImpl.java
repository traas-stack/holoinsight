/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AlertDingDingRobotService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmDingDingRobotDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/4/18 11:02 下午
 */
@RestController
@RequestMapping("/webapi/alarmDingDingRobot")
public class AlarmDingDingRobotFacadeImpl extends BaseFacade {

  public static final String dingdingUrlPrefix =
      "https://oapi.dingtalk.com/robot/send?access_token=";

  @Autowired
  private AlertDingDingRobotService alarmDingDingRobotService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmDingDingRobotDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmDingDingRobotFacadeImplChecker")
  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> create(@RequestBody AlarmDingDingRobotDTO alarmDingDingRobotDTO) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmDingDingRobotDTO.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          alarmDingDingRobotDTO.setTenant(ms.tenant);
        }
        alarmDingDingRobotDTO.setWorkspace(requestContextAdapter.getWorkspace(true));
        alarmDingDingRobotDTO.setGmtCreate(new Date());
        alarmDingDingRobotDTO.setGmtModified(new Date());
        Long id = alarmDingDingRobotService.save(alarmDingDingRobotDTO);

        userOpLogService.append("alarm_dingding_robot", id, OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(alarmDingDingRobotDTO), null, null,
            "alarm_dingding_robot_create");

        JsonResult.createSuccessResult(result, id);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmDingDingRobotDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmDingDingRobotFacadeImplChecker")
  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmDingDingRobotDTO alarmDingDingRobotDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {

        AlarmDingDingRobotDTO item = alarmDingDingRobotService
            .queryById(alarmDingDingRobotDTO.getId(), RequestContext.getContext().ms.getTenant());

        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmDingDingRobotDTO.setModifier(mu.getLoginName());
        }
        MonitorScope ms = RequestContext.getContext().ms;

        alarmDingDingRobotDTO.setGmtModified(new Date());
        boolean save = alarmDingDingRobotService.updateById(alarmDingDingRobotDTO);

        userOpLogService.append("alarm_dingding_robot", item.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(item),
            J.toJson(alarmDingDingRobotDTO), null, "alarm_dingding_robot_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmDingDingRobotFacadeImplChecker")
  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmDingDingRobotDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmDingDingRobotDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {

        AlarmDingDingRobotDTO save =
            alarmDingDingRobotService.queryById(id, RequestContext.getContext().ms.getTenant());
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmDingDingRobotFacadeImplChecker")
  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        boolean rtn = false;
        AlarmDingDingRobotDTO alarmDingDingRobot =
            alarmDingDingRobotService.queryById(id, ms.getTenant());
        if (alarmDingDingRobot != null
            && StringUtils.equals(alarmDingDingRobot.getTenant(), ms.getTenant())) {
          rtn = alarmDingDingRobotService.removeById(id);
        }

        userOpLogService.append("alarm_dingding_robot", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(alarmDingDingRobot), null, null, "alarm_dingding_robot_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmDingDingRobotFacadeImplChecker")
  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmDingDingRobotDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmDingDingRobotDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }
        pageRequest.getTarget().setWorkspace(requestContextAdapter.getWorkspace(true));
        JsonResult.createSuccessResult(result,
            alarmDingDingRobotService.getListByPage(pageRequest));
      }
    });

    return result;
  }
}
