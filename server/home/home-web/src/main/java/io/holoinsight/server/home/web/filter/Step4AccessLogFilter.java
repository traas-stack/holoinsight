/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.filter;

import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.common.ResponseUtil;
import io.holoinsight.server.home.web.wrapper.CountServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xiangwanpeng
 * @version : Step4AccessLogFilter.java, v 0.1 2022年12月06日 13:50 xiangwanpeng Exp $
 */
@Configuration
@Order(6)
@Slf4j
public class Step4AccessLogFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(Step4AccessLogFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) servletRequest;

    HttpServletResponse resp = (HttpServletResponse) servletResponse;

    String path = new UrlPathHelper().getLookupPathForRequest(req);
    String client = getRemoteAddr(req);
    String method = req.getMethod();
    long start = System.currentTimeMillis();

    CountServletResponseWrapper responseWrapper = new CountServletResponseWrapper(resp);
    try {
      filterChain.doFilter(req, responseWrapper);
    } catch (Exception e) {
      ResponseUtil.authFailedResponse(resp, HttpStatus.BAD_REQUEST.value(), e.getMessage(),
          ResultCodeEnum.MONITOR_SYSTEM_ERROR);
      logger.error(e.getMessage(), e);
    }
    StringBuilder builder = new StringBuilder();
    builder.append(RequestContext.getTrace());
    builder.append("client=").append(client == null ? "-" : client).append(",");
    builder.append("path=").append(path == null ? "-" : urlFormat(path)).append(",");
    builder.append("method=").append(method == null ? "-" : method).append(",");
    int status = resp.getStatus();
    builder.append("status=").append(status).append(",");
    int size = responseWrapper.getSize();
    builder.append("size=").append(size).append(",");
    long cost = System.currentTimeMillis() - start;
    builder.append("cost=").append(cost).append("ms,");
    if (logger.isInfoEnabled()) {
      String log = builder.toString();
      logger.info(log);
    }
  }

  @Override
  public void destroy() {}

  private String getRemoteAddr(HttpServletRequest request) {
    String ip;
    @SuppressWarnings("unchecked")
    Enumeration<String> xffs = request.getHeaders("X-Forwarded-For");
    if (xffs.hasMoreElements()) {
      String xff = xffs.nextElement();
      ip = resolveClientIPFromXFF(xff);
      if (isValidIP(ip)) {
        return ip;
      }
    }
    ip = request.getHeader("Proxy-Client-IP");
    if (isValidIP(ip)) {
      return ip;
    }
    ip = request.getHeader("WL-Proxy-Client-IP");
    if (isValidIP(ip)) {
      return ip;
    }
    return request.getRemoteAddr();
  }

  /**
   * 从X-Forwarded-For头部中获取客户端的真实IP。 X-Forwarded-For并不是RFC定义的标准HTTP请求Header
   * ，可以参考http://en.wikipedia.org/wiki/X-Forwarded-For
   *
   * @param xff X-Forwarded-For头部的值
   * @return 如果能够解析到client IP，则返回表示该IP的字符串，否则返回null
   */
  private String resolveClientIPFromXFF(String xff) {
    if (xff == null || xff.isEmpty()) {
      return null;
    }
    String[] ss = xff.split(",");
    for (int i = ss.length - 1; i >= 0; i--) { // x-forward-for链反向遍历
      String ip = ss[i].trim();
      if (isValidIP(ip)) {
        return ip;
      }
    }

    return null;
  }

  private static final Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

  private boolean isValidIP(String ip) {
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      return false;
    }
    return ipPattern.matcher(ip).matches();
  }

  private Map<String, String> getRequestParamValueMap(HttpServletRequest request) {
    Map<String, String> param2value = new HashMap<>();
    Enumeration e = request.getParameterNames();
    String param;

    while (e.hasMoreElements()) {
      param = (String) e.nextElement();
      if (param != null) {
        String value = request.getParameter(param);
        if (value != null) {
          param2value.put(param, value);
        }
      }
    }

    return param2value;
  }

  private static String urlFormat(String path) {
    return path;
  }
}
