/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.filter;

import io.holoinsight.server.home.web.config.RestAuthUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.CookieUtils;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.Set;

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
    try {
      boolean next = auth(req, resp);
      if (next) {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    } catch (Throwable e) {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "auth check error, " + e.getMessage());
      log.error("{} auth check error, auth info: {}", RequestContext.getTrace(),
          J.toJson(J.toJson(RequestContext.getContext())), e);
    } finally {
      Debugger.print("Step3AuthFilter", "CTX: " + J.toJson(J.toJson(RequestContext.getContext())));
    }
  }

  public boolean auth(HttpServletRequest req, HttpServletResponse resp) throws Throwable {

    if (RestAuthUtil.singleton.isNoAuthRequest(req) || !RestAuthUtil.singleton.isAuthRequest(req)) {
      return true;
    }
    MonitorUser mu = (MonitorUser) req.getAttribute(MonitorUser.MONITOR_USER);

    // token类的直接过
    if (IdentityType.OUTTOKEN.equals(mu.getIdentityType()) || MetaDictUtil.getUlaClose()) {
      String token = req.getHeader("apiToken");
      // 接口权限判定
      if (!ulaFacade.authFunc(req) && StringUtil.isBlank(token)) {
        log.warn("{} authFunc check failed", RequestContext.getTrace());
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            RequestContext.getTrace() + " authFunc check failed");
        return false;
      }
      Context c = new Context(RequestContext.getContext().ms, mu, new MonitorAuth(),
          RequestContext.getContext().mp);
      RequestContext.setContext(c);
      return true;
    }

    // 获取一个用户的权限包
    MonitorScope ms = MonitorCookieUtil.getScope(req, mu);
    MonitorAuth ma = null;

    try {
      ma = MonitorCookieUtil.getMonitorAuthCookie(req);
      if (ma != null && !CollectionUtils.isEmpty(ma.powerConstants)) {

        // 用户 cookies 里面拥有该租户权限，此时默认返回 true
        Map<String, Set<PowerConstants>> tenantMaps = ma.getTenantViewPowerList();
        if (tenantMaps.containsKey(ms.getTenant())) {
          req.setAttribute(MonitorAuth.MONITOR_AUTH, ma);
          return true;
        }
      }
      ma = ulaFacade.getUserPowerPkg(mu, ms);
      if (null == ma || CollectionUtils.isEmpty(ma.powerConstants)
          || CollectionUtils.isEmpty(ma.getTenantViewPowerList())) {
        log.error("check tenant auth failed, " + J.toJson(ma));
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "check tenant auth failed");
        return false;
      }

      if (!ma.getTenantViewPowerList().containsKey(ms.getTenant())) {
        log.error("check tenant " + ms.getTenant() + " is not auth, " + J.toJson(ma));
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            "check tenant " + ms.getTenant() + " is not auth");
        return false;
      }

      MonitorCookieUtil.addMonitorAuthCookie(ma, resp);
      MonitorCookieUtil.addUserCookie(mu, resp);
      // 放到线程上下文中
    } catch (Throwable e) {
      log.error("auth failed by cookie", e);
      return false;
    } finally {
      Context c = new Context(ms, mu, ma);
      Debugger.print("Step3AuthFilter", "MS: " + J.toJson(ms));
      Debugger.print("Step3AuthFilter", "MU: " + J.toJson(mu));
      Debugger.print("Step3AuthFilter", "MA: " + J.toJson(ma));
      RequestContext.setContext(c);
    }
    return true;
  }

}
