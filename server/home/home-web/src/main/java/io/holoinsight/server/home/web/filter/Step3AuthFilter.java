/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.filter;

import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.MonitorParams;
import io.holoinsight.server.home.web.config.RestAuthUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static io.holoinsight.server.home.web.common.ResponseUtil.authFailedResponse;

/**
 *
 * @author jsy1001de
 * @version 1.0: AuthFilter.java, v 0.1 2022年03月14日 5:35 下午 jsy1001de Exp $
 */
@Configuration
@Order(5)
@Slf4j
public class Step3AuthFilter implements Filter {

  @Autowired
  private ULAFacade ulaFacade;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    HttpServletResponse resp = (HttpServletResponse) servletResponse;
    boolean next;
    try {
      next = auth(req, resp);
    } catch (Throwable e) {
      authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN,
          "auth check error, " + e.getMessage(), ResultCodeEnum.MONITOR_SYSTEM_ERROR);
      log.error("{} auth check error, auth info: {}", RequestContext.getTrace(),
          J.toJson(J.toJson(RequestContext.getContext())), e);
      return;
    } finally {
      Debugger.print("Step3AuthFilter", "CTX: " + J.toJson(J.toJson(RequestContext.getContext())));
    }
    if (next) {
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

  public boolean auth(HttpServletRequest req, HttpServletResponse resp) throws Throwable {

    if (RestAuthUtil.singleton.isNoAuthRequest(req) || !RestAuthUtil.singleton.isAuthRequest(req)
        || MetaDictUtil.getTokenUrlNoAuth().contains(req.getServletPath())) {
      return true;
    }
    MonitorUser mu = (MonitorUser) req.getAttribute(MonitorUser.MONITOR_USER);

    // token类的直接过
    if (IdentityType.OUTTOKEN.equals(mu.getIdentityType()) || MetaDictUtil.getUlaClose()) {
      String token = req.getHeader("apiToken");
      // 接口权限判定
      if (!ulaFacade.authFunc(req) && StringUtil.isBlank(token)) {
        log.warn("{} authFunc check failed", RequestContext.getTrace());
        authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN, "权限不足，请联系账号管理员",
            ResultCodeEnum.AUTH_CHECK_ERROR);
        return false;
      }
      Context c = new Context(RequestContext.getContext().ms, mu, new MonitorAuth(),
          RequestContext.getContext().mp);
      RequestContext.setContext(c);
      return true;
    }

    // 获取一个用户的权限包
    MonitorScope ms = ulaFacade.getMonitorScope(req, mu);
    MonitorParams mp = ulaFacade.getMonitorParams(req, mu);
    MonitorAuth ma = null;

    try {
      ma = MonitorCookieUtil.getMonitorAuthCookie(req);
      if (ma != null && !CollectionUtils.isEmpty(ma.powerConstants)) {
        // 用户 cookies 里面拥有该租户权限，此时默认返回 true
        Set<String> tenantMaps = ma.hasTenantViewPowerList();
        Set<String> workspaceMaps = ma.hasWsViewPowerList();
        boolean checkCache = false;
        if (StringUtils.isBlank(ms.getWorkspace()) && CollectionUtils.isEmpty(workspaceMaps)
            && tenantMaps.contains(ms.getTenant())) {
          req.setAttribute(MonitorAuth.MONITOR_AUTH, ma);
          checkCache = true;
        } else if (StringUtils.isNotBlank(ms.getWorkspace()) && tenantMaps.contains(ms.getTenant())
            && workspaceMaps.contains(ms.getWorkspace())) {
          req.setAttribute(MonitorAuth.MONITOR_AUTH, ma);
          checkCache = true;
        }
        if (checkCache) {
          ulaFacade.checkWorkspace(req, mu, ms, mp);
          return true;
        }
      }
      ma = ulaFacade.getUserPowerPkg(req, mu, ms);
      ulaFacade.checkWorkspace(req, mu, ms, mp);
      if (null == ma || CollectionUtils.isEmpty(ma.powerConstants)
          || CollectionUtils.isEmpty(ma.getTenantViewPowerList())) {
        log.error("check tenant auth failed, " + J.toJson(ma));
        authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN, "check tenant auth failed",
            ResultCodeEnum.AUTH_CHECK_ERROR);
        return false;
      }

      if (!ma.getTenantViewPowerList().containsKey(ms.getTenant())) {
        log.error("check tenant " + ms.getTenant() + " is not auth, " + J.toJson(ma));
        authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN,
            "check tenant " + ms.getTenant() + " is not auth", ResultCodeEnum.AUTH_CHECK_ERROR);
        return false;
      }

      MonitorCookieUtil.addMonitorAuthCookie(ma, resp);
      MonitorCookieUtil.addUserCookie(mu, resp);
      // 放到线程上下文中
    } catch (MonitorException me) {
      log.error("auth failed by exception", me);
      if (me.getResultCode() == ResultCodeEnum.NO_LOGIN_AUTH) {
        authFailedResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, me.getMessage(),
            ResultCodeEnum.NO_LOGIN_AUTH);
      } else if (me.getResultCode() == ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR) {
        authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN, me.getMessage(),
            ResultCodeEnum.DOWNSTREAM_AUTH_SYSTEM_ERROR);
      } else {
        authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN,
            "auth failed by exception, " + me.getMessage(),
            ResultCodeEnum.MONITOR_AUTH_SYSTEM_ERROR);
      }
    } catch (Throwable e) {
      log.error("auth failed by cookie", e);
      authFailedResponse(resp, HttpServletResponse.SC_FORBIDDEN,
          "auth failed by cookie, " + e.getMessage(), ResultCodeEnum.MONITOR_SYSTEM_ERROR);
      return false;
    } finally {
      Context c = new Context(ms, mu, ma, mp);
      Debugger.print("Step3AuthFilter",
          "MS: " + J.toJson(ms) + ", MU: " + J.toJson(mu) + ", MA: " + J.toJson(ma));
      RequestContext.setContext(c);
    }
    return true;
  }

}
