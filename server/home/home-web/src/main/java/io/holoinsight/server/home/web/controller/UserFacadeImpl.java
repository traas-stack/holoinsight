/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.biz.service.MarketplacePluginService;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTarget;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.UserAuthScope;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

  @Autowired
  private MarketplacePluginService marketplacePluginService;

  @ResponseBody
  // @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
        resultObj.put("tPowers", ma.getTenantViewPowerList());
        resultObj.put("tenants", ulaFacade.getCurrentULA().getUserTenants(mu));
        resultObj.put("loginUrl", ulaFacade.getCurrentULA().getLoginUrl());

        resultObj.put("marketplaces",
            marketplacePluginService.queryByTenant(ms.tenant, ms.workspace));

        Debugger.print("UserFacadeImpl", "getCurrentUser: " + J.toJson(resultObj));
        JsonResult.createSuccessResult(result, resultObj);
      }
    });

    return result;

  }

  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  @GetMapping(value = "/authByLoginName/{loginName}")
  public JsonResult<Object> authByLoginName(@PathVariable("loginName") String loginName) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(loginName, "loginName");
      }

      @Override
      public void doManage() {
        log.info("query login name:" + loginName);
        Context context = RequestContext.getContext();
        MonitorUser mu = null;
        MonitorScope ms = context.ms;
        if (context.mu != null
            && (StringUtils.isBlank(loginName) || loginName.equals(context.mu.getLoginName()))) {
          mu = context.mu;
        } else {
          try {
            mu = ulaFacade.getByLoginName(loginName);
          } catch (Throwable e) {
            log.error("get user info error", e);
            JsonResult.createFailResult(result, "get user info error" + e.getMessage(),
                ResultCodeEnum.SYSTEM_ERROR.name());
            return;
          }
        }

        try {
          MonitorAuth auth = ulaFacade.getUserPowerPkg(mu, ms);
          // 租户的
          Map<String, List<PowerConstants>> tPowers = new HashMap<>();

          for (Entry<AuthTarget, Set<PowerConstants>> tpc : auth.powerConstants.entrySet()) {
            AuthTarget at = tpc.getKey();
            List<PowerConstants> pcs = new ArrayList<>(tpc.getValue());
            if (at.authTargetType.equals(AuthTargetType.TENANT)) {
              tPowers.put(at.quthTargetId, pcs);
            }
          }

          UserAuthScope uas = new UserAuthScope(tPowers, mu.isSuperAdmin(), mu.isSuperViewer());
          JsonResult.createSuccessResult(result, uas);
        } catch (Throwable e) {
          log.error("get user auth failed", e);
          JsonResult.createFailResult(result, "get user auth failed" + e.getMessage(),
              ResultCodeEnum.SYSTEM_ERROR.name());
        }
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
        JsonResult.createSuccessResult(result, ulaFacade.getCurrentULA().getUsers(mu, ms));
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
        JsonResult.createSuccessResult(result, ulaFacade.getCurrentULA().getByLoginName(loginName));
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
        JsonResult.createSuccessResult(result, ulaFacade.getCurrentULA().getByUserId(userId));
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
