/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
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
 *
 * @author jsy1001de
 * @version 1.0: AlarmGroupFacadeImpl.java, v 0.1 2022年04月08日 2:56 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/alarmGroup")
@TokenUrls("/webapi/alarmGroup/query")
public class AlarmGroupFacadeImpl extends BaseFacade {

  @Autowired
  private AlertGroupService alarmGroupService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmGroupDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmGroupDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmGroupDTO>> result = new JsonResult<>();
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
        JsonResult.createSuccessResult(result, alarmGroupService.getListByPage(pageRequest));
      }
    });

    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody AlarmGroupDTO alarmGroup) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(alarmGroup.getGroupName(), "groupName");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmGroup.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          alarmGroup.setTenant(ms.tenant);
        }
        alarmGroup.setGmtCreate(new Date());
        alarmGroup.setGmtModified(new Date());
        Long rtn = alarmGroupService.save(alarmGroup);
        userOpLogService.append("alarm_group", rtn, OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(alarmGroup), null, null,
            "alarm_group_create");

        JsonResult.createSuccessResult(result, rtn);
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmGroupDTO alarmGroup) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmGroup.getId(), "id");
        ParaCheckUtil.checkParaNotNull(alarmGroup.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(alarmGroup.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

      }

      @Override
      public void doManage() {

        AlarmGroupDTO item = alarmGroupService.queryById(alarmGroup.getId(),
            RequestContext.getContext().ms.getTenant());
        if (null == item) {
          throw new MonitorException("cannot find record: " + alarmGroup.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(alarmGroup.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }

        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != mu) {
          alarmGroup.setModifier(mu.getLoginName());
        }
        alarmGroup.setGmtModified(new Date());
        boolean save = alarmGroupService.updateById(alarmGroup);

        userOpLogService.append("alarm_group", alarmGroup.getId(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(item), J.toJson(alarmGroup), null, "alarm_group_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<AlarmGroupDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmGroupDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {

        AlarmGroupDTO save =
            alarmGroupService.queryById(id, RequestContext.getContext().ms.getTenant());
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        boolean rtn = false;
        AlarmGroupDTO alarmGroup = alarmGroupService.queryById(id, ms.getTenant());
        if (alarmGroup != null) {
          rtn = alarmGroupService.removeById(id);
        }

        userOpLogService.append("alarm_group", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(alarmGroup), null, null, "alarm_group_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }
}
