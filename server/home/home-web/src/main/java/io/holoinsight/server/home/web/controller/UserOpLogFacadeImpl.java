/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.page.MonitorTimePageRequest;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.UserOpLog;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserOpLogFacadeImpl.java, v 0.1 2022年03月21日 3:51 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/userOpLog")
@Slf4j
public class UserOpLogFacadeImpl extends BaseFacade {

  @Autowired
  private UserOpLogService userOpLogService;

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<UserOpLog> queryById(@PathVariable("id") Long id) {
    final JsonResult<UserOpLog> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        UserOpLog userOpLog = userOpLogService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == userOpLog) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record:" + id);
        }
        JsonResult.createSuccessResult(result, userOpLog);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<UserOpLog>> pageQuery(
      @RequestBody MonitorTimePageRequest<UserOpLog> userOpLogRequest) {
    final JsonResult<MonitorPageResult<UserOpLog>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(userOpLogRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isBlank(ms.tenant)) {
          userOpLogRequest.getTarget().setTenant(ms.tenant);
        }
        // if (null != ms && !StringUtils.isBlank(ms.workspace)) {
        // userOpLogRequest.getTarget().setWorkspace(ms.workspace);
        // }
        JsonResult.createSuccessResult(result, userOpLogService.getListByPage(userOpLogRequest));
      }
    });

    return result;
  }

}
