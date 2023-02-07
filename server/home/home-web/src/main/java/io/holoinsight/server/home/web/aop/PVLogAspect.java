/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jsy1001de
 * @version 1.0: PVLogAspect.java, v 0.1 2022年02月22日 6:03 下午 jinsong.yjs Exp $
 */
@Aspect
@Component
public class PVLogAspect {
  private Logger logger = LoggerFactory.getLogger(PVLogAspect.class);

  private static ThreadLocal<WebRequestContext> threadLocalContext =
      new ThreadLocal<WebRequestContext>();

  @Pointcut("execution(public * io.holoinsight.server.home.web.controller..*.*(..))")
  public void pvLog() {}

  @Before("pvLog()")
  public void doBefore(JoinPoint joinPoint) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    WebRequestContext ctx = new WebRequestContext();
    ctx.URI = request.getRequestURI();
    ctx.method = request.getMethod();
    ctx.remoteIP = request.getRemoteAddr();
    ctx.start = System.currentTimeMillis();
    threadLocalContext.set(ctx);
  }

  @AfterReturning("pvLog()")
  public void doAfter() {
    WebRequestContext ctx = threadLocalContext.get();
    Long cost = System.currentTimeMillis() - ctx.start;

    logger.info("[PV] url=[" + ctx.URI + "], method=[" + ctx.method + "], cost=[" + cost + "ms]");
  }

  public static class WebRequestContext {
    public String URI;
    public String method;
    public Long start;
    public String remoteIP;
  }
}
