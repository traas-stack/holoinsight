/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserFacadeImpl.java, v 0.1 2022年03月15日 5:51 下午 jsy1001de Exp $
 */
@RestController
@RequestMapping("/webapi/user")
@Slf4j
public class UserFacadeImpl extends BaseFacade {

  @Autowired
  private ULAFacade ulaFacade;

  @ResponseBody
  @GetMapping(value = "/getCurrentUser")
  public JsonResult<Object> getCurrentUser() {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {

        MonitorUser mu = RequestContext.getContext().mu;
        MonitorAuth ma = RequestContext.getContext().ma;
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> resultObj = new HashMap<>();
        resultObj.put("user", mu);
        resultObj.put("scope", ms);
        resultObj.put("tPowers", ma.getTenantViewPowerList());
        resultObj.put("tenants", ulaFacade.getCurrentULA().getUserTenants(mu));
        Debugger.print("UserFacadeImpl", "getCurrentUser: " + J.toJson(resultObj));
        JsonResult.createSuccessResult(result, resultObj);
      }
    });

    return result;

  }

  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  @GetMapping(value = "/getTenantUsers")
  public JsonResult<List<MonitorUser>> getUsers() {
    final JsonResult<List<MonitorUser>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;
        JsonResult.createSuccessResult(result, ulaFacade.getUsers(mu, ms));
      }
    });

    return result;

  }

  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  @GetMapping(value = "/getTenantUser")
  public JsonResult<MonitorUser> getTenantUser(@RequestParam("loginName") String loginName) {
    final JsonResult<MonitorUser> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result, ulaFacade.getByLoginName(loginName));
      }
    });

    return result;

  }

  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  @GetMapping(value = "/getTenantUserById")
  public JsonResult<MonitorUser> getTenantUserById(@RequestParam("userId") String userId) {
    final JsonResult<MonitorUser> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result, ulaFacade.getByUserId(userId));
      }
    });

    return result;

  }

  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  @GetMapping(value = "/logout")
  public JsonResult<Boolean> logout(HttpServletRequest req, HttpServletResponse resp) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        ulaFacade.getCurrentULA().logout(req, resp);
        req.getSession().invalidate();
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;

  }
}
