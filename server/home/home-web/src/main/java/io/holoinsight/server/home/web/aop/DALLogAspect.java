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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DALLogAspect.java, v 0.1 2022年02月22日 6:16 下午 jinsong.yjs Exp $
 */
@Aspect
@Component
public class DALLogAspect {
  private Logger logger = LoggerFactory.getLogger(DALLogAspect.class);

  private static ThreadLocal<DALContext> threadLocalContext = new ThreadLocal<DALContext>();

  @Pointcut("execution(public * com.alipay.cloudmonitor.prod.dal.repository..*.*(..))")
  public void dalInnerLog() {}

  @Pointcut("execution(public * com.alipay.cloudmonitor.prod.dal.repository..*.*(..))")
  public void dalOuterLog() {}

  @Pointcut("dalInnerLog() || dalOuterLog()")
  public void dalLog() {}

  @Before("dalLog()")
  public void doBefore(JoinPoint joinPoint) {
    Long start = System.currentTimeMillis();
    DALContext ctx = new DALContext();
    joinPoint.getSignature().getDeclaringType();
    ctx.clazzName = joinPoint.getSignature().getDeclaringTypeName();
    ctx.methodName = joinPoint.getSignature().getName();
    ctx.start = start;
    threadLocalContext.set(ctx);
  }

  @AfterReturning(pointcut = "dalLog()", returning = "result")
  public void doAfter(Object result) {
    DALContext ctx = threadLocalContext.get();
    Long cost = System.currentTimeMillis() - ctx.start;

    StringBuilder builder = new StringBuilder();
    builder.append("[DAL] method=[").append(ctx.clazzName).append(".").append(ctx.methodName)
        .append("],Y, cost=[").append(cost).append("ms]");

    if (ctx.methodName.startsWith("findBy")) {
      if (result instanceof ArrayList<?>) {
        List<?> list = (ArrayList<?>) result;
        builder.append(", size=[").append(list.size()).append("]");
      }
    }

    logger.info(builder.toString());
  }

  public static class DALContext {
    public String clazzName;
    public String methodName;
    public Long start;
  }
}
