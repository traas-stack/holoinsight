/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Sets;

/**
 * 该 filter 用于拦截那些直接通过 servlet 定义的处理器
 * <p>created at 2022/11/18
 *
 * @author xzchaoo
 */
@WebFilter(urlPatterns = "/")
public class InternalWebApiFilter extends OncePerRequestFilter {
    private static final Set<String>               URIS = Sets.newHashSet("/metrics");
    @Autowired
    private              InternalWebApiInterceptor internalWebApiInterceptor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (internalWebApiInterceptor.isInternal(request) || URIS.contains(uri)) {
            if (!internalWebApiInterceptor.isSafeAccess(request)) {
                internalWebApiInterceptor.writeForbiddenResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
