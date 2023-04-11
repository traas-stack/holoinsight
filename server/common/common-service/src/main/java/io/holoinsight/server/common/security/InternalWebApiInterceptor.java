/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import com.google.common.collect.Sets;
import io.holoinsight.server.common.NetUtils;
import io.holoinsight.server.common.web.InternalWebApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 该 interceptor 用于拦截通过 spring mvc 定义的处理器
 * <p>
 * created at 2022/11/11
 *
 * @author xzchaoo
 */
public class InternalWebApiInterceptor implements HandlerInterceptor {
  static final Set<String> LOCALHOST = Sets.newHashSet("localhost", "127.0.0.1", "0:0:0:0:0:0:0:1");
  private static final Set<String> INTERNAL_WHITE_HOSTS =
      Sets.newHashSet("gateway.holoinsight-gateway", "gateway.holoinsight-server");

  @Autowired
  private HoloinsightSecurityProperties properties;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (handler instanceof HandlerMethod) {
      HandlerMethod hm = (HandlerMethod) handler;
      if (isInternal(request) || isInternal(hm)) {
        if (!isSafeAccess(request)) {
          writeForbiddenResponse(response);
          return false;
        }
      }
    }
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

  private boolean isInternal(HandlerMethod hm) {
    return AnnotatedElementUtils.hasAnnotation(hm.getBeanType(), InternalWebApi.class)
        || hm.getMethodAnnotation(InternalWebApi.class) != null;
  }

  boolean isInternal(HttpServletRequest request) {
    String uri = new UrlPathHelper().getLookupPathForRequest(request);
    return uri.startsWith("/internal/");
  }

  boolean isSafeAccess(HttpServletRequest request) {
    String forwardFor = request.getHeader("x-forward-for");
    if (StringUtils.isNotEmpty(forwardFor)) {
      return false;
    }
    String host = StringUtils.substringBefore(request.getHeader("host"), ":");
    if (host != null && INTERNAL_WHITE_HOSTS.contains(host)) {
      return true;
    }
    if (host != null && properties.getWhiteHosts().contains(host)) {
      return true;
    }
    String localIp = NetUtils.getLocalIp();
    String addr = request.getRemoteAddr();
    return LOCALHOST.contains(addr) || StringUtils.equals(host, localIp);
  }

  void writeForbiddenResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println("forbidden");
  }
}
