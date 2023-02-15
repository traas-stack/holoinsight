/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.holoinsight.server.common.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Sets;
import org.springframework.web.util.UrlPathHelper;

/**
 * 该 filter 用于拦截那些直接通过 servlet 定义的处理器
 * <p>
 * created at 2022/11/18
 *
 * @author xzchaoo
 */
@WebFilter(urlPatterns = "/")
public class InternalWebApiFilter extends OncePerRequestFilter {
  private static Logger LOGGER = LoggerFactory.getLogger(InternalWebApiFilter.class);

  private static final Set<String> URIS = Sets.newHashSet("/metrics");
  @Autowired
  private InternalWebApiInterceptor internalWebApiInterceptor;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String uri = new UrlPathHelper().getLookupPathForRequest(request);

    if (internalWebApiInterceptor.isInternal(request) || URIS.contains(uri)) {
      if (!internalWebApiInterceptor.isSafeAccess(request)) {
        internalWebApiInterceptor.writeForbiddenResponse(response);
        return;
      }
    }
    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      Map<String, String[]> parameterMap = request.getParameterMap();
      LOGGER.error("fail to do filter {} for {}, parameter {}", uri, e.getMessage(),
          J.toJson(parameterMap), e);
    }

  }
}
