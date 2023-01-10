/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zanghaibo
 * @time 2022-11-01 4:33 下午
 */
@Slf4j
public class RefererInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String referer = request.getHeader("referer");
    if (referer != null) {
      referer = replaceToHttp(referer);
    }
    StringBuffer sb = new StringBuffer();
    String requestUrl =
        sb.append(request.getScheme()).append("://").append(request.getServerName()).toString();
    if (referer == null || referer.equals("")
        || referer.lastIndexOf(replaceToHttp(requestUrl)) == 0) {
      return true;
    } else {
      log.info("Find csrf request [Referer:" + referer + " Request: " + requestUrl + "]");
      return false;
    }
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

  }

  private String replaceToHttp(String url) {
    String dest = url;
    if (url.startsWith("https:")) {
      dest = url.replace("https:", "http:");
    }
    return dest;
  }
}
