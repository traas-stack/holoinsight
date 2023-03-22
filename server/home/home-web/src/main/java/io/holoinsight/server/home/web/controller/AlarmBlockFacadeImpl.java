/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlertBlockService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmBlockDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
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
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmBlockDTO.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          alarmBlockDTO.setTenant(ms.tenant);
        }

        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          alarmBlockDTO.setWorkspace(ms.workspace);
        }
        alarmBlockDTO.setGmtCreate(new Date());
        alarmBlockDTO.setGmtModified(new Date());
        Long id = alarmBlockService.save(alarmBlockDTO);

        userOpLogService.append("alarm_block", id, OpType.CREATE, mu.getLoginName(), ms.getTenant(),
            ms.getWorkspace(), J.toJson(alarmBlockDTO), null, null, "alarm_block_create");

        JsonResult.createSuccessResult(result, id);
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmBlockDTO alarmBlockDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmBlockDTO.getId(), "id");
        ParaCheckUtil.checkParaNotNull(alarmBlockDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(alarmBlockDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlarmBlockDTO item =
            alarmBlockService.queryById(alarmBlockDTO.getId(), ms.getTenant(), ms.getWorkspace());
        if (null == item) {
          throw new MonitorException("cannot find record: " + alarmBlockDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(alarmBlockDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }

        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmBlockDTO.setModifier(mu.getLoginName());
        }

        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          alarmBlockDTO.setWorkspace(ms.getWorkspace());
        }
        alarmBlockDTO.setGmtModified(new Date());
        boolean save = alarmBlockService.updateById(alarmBlockDTO);
        userOpLogService.append("alarm_block", item.getId(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(item), J.toJson(alarmBlockDTO), null, "alarm_block_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<AlarmBlockDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmBlockDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlarmBlockDTO save = alarmBlockService.queryById(id, ms.getTenant(), ms.getWorkspace());
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
        boolean rtn = false;
        MonitorScope ms = RequestContext.getContext().ms;
        AlarmBlockDTO alarmBlockDTO =
            alarmBlockService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (alarmBlockDTO != null) {
          rtn = alarmBlockService.removeById(id);
        }

        userOpLogService.append("alarm_block", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(alarmBlockDTO), null, null, "alarm_block_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmBlockDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmBlockDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmBlockDTO>> result = new JsonResult<>();
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
        JsonResult.createSuccessResult(result, alarmBlockService.getListByPage(pageRequest));
      }
    });

    return result;
  }

}
