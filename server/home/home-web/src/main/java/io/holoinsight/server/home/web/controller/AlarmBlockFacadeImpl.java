/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.service.AlertBlockService;
import io.holoinsight.server.common.service.UserOpLogService;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.common.dao.entity.dto.AlarmBlockDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
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
 * @date 2022/4/1 10:27 上午
 */
@RestController
@RequestMapping("/webapi/alarmBlock")
public class AlarmBlockFacadeImpl extends BaseFacade {

  @Autowired
  private AlertBlockService alarmBlockService;

  @Autowired
  private UserOpLogService userOpLogService;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmBlockDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody AlarmBlockDTO alarmBlockDTO) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmBlockDTO.setCreator(mu.getLoginName());
        }
        if (StringUtils.isNotEmpty(tenant)) {
          alarmBlockDTO.setTenant(tenant);
        }

        if (StringUtils.isNotEmpty(workspace)) {
          alarmBlockDTO.setWorkspace(workspace);
        }
        alarmBlockDTO.setGmtCreate(new Date());
        alarmBlockDTO.setGmtModified(new Date());
        Long id = alarmBlockService.save(alarmBlockDTO);

        userOpLogService.append("alarm_block", id, OpType.CREATE, mu.getLoginName(), tenant,
            workspace, J.toJson(alarmBlockDTO), null, null, "alarm_block_create");

        JsonResult.createSuccessResult(result, id);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmBlockDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmBlockDTO alarmBlockDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        AlarmBlockDTO item = alarmBlockService.queryById(alarmBlockDTO.getId(), tenant, workspace);

        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmBlockDTO.setModifier(mu.getLoginName());
        }

        if (StringUtils.isNotEmpty(workspace)) {
          alarmBlockDTO.setWorkspace(workspace);
        }
        alarmBlockDTO.setGmtModified(new Date());
        boolean save = alarmBlockService.updateById(alarmBlockDTO);
        userOpLogService.append("alarm_block", item.getId(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), tenant, workspace, J.toJson(item),
            J.toJson(alarmBlockDTO), null, "alarm_block_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmBlockDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmBlockDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        AlarmBlockDTO save = alarmBlockService.queryById(id, tenant, workspace);
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        boolean rtn = false;
        String tenant = tenant();
        String workspace = workspace();
        AlarmBlockDTO alarmBlockDTO = alarmBlockService.queryById(id, tenant, workspace);
        if (alarmBlockDTO != null) {
          rtn = alarmBlockService.removeById(id);
        }

        userOpLogService.append("alarm_block", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), tenant, workspace,
            J.toJson(alarmBlockDTO), null, null, "alarm_block_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmBlockDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmBlockDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmBlockDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        if (StringUtils.isNotEmpty(tenant)) {
          pageRequest.getTarget().setTenant(tenant);
        }
        if (StringUtils.isNotEmpty(workspace)) {
          pageRequest.getTarget().setWorkspace(workspace);
        }
        JsonResult.createSuccessResult(result, alarmBlockService.getListByPage(pageRequest));
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!ruleId"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmBlockFacadeImplChecker")
  @GetMapping("/queryByRuleId/{ruleId}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmBlockDTO> queryByRuleId(@PathVariable("ruleId") String ruleId) {
    final JsonResult<AlarmBlockDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        AlarmBlockDTO save = alarmBlockService.queryByRuleId(ruleId, tenant(), workspace());
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }
}
