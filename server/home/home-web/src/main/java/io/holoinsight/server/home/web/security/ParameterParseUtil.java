/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import io.holoinsight.server.common.J;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;

/**
 * @author masaimu
 * @version 2023-12-28 20:41:00
 */
public class ParameterParseUtil {

  /**
   * 从methodInvocation的参数中提取出HttpServletRequest对象
   * 
   * @param methodInvocation
   * @return
   */
  public static HttpServletRequest getServletRequest(MethodInvocation methodInvocation) {
    Object[] arguments = methodInvocation.getArguments();
    for (Object argument : arguments) {
      if (argument instanceof HttpServletRequest) {
        return (HttpServletRequest) argument;
      }
    }
    return null;
  }

  /**
   * 通过配置获取某个参数的值
   * 
   * @param methodInvocation
   * @param parameterName
   * @param parameterNameDiscoverer
   * @return
   */
  public static String getParameterFromArguments(MethodInvocation methodInvocation,
      String parameterName, ParameterNameDiscoverer parameterNameDiscoverer) {
    // 解析需要解析的
    StringWriter sw = new StringWriter();
    VelocityContext context = getEvaluationContext(methodInvocation, parameterNameDiscoverer);
    try {
      Velocity.evaluate(context, sw, "", parameterName);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return sw.toString();
  }

  /**
   * 获取velocity上下文，解析参数值
   * 
   * @param methodInvocation
   * @param parameterNameDiscoverer
   * @return
   */
  public static VelocityContext getEvaluationContext(MethodInvocation methodInvocation,
      ParameterNameDiscoverer parameterNameDiscoverer) {
    VelocityContext context = new VelocityContext();
    Object[] args = methodInvocation.getArguments();
    for (int i = 0; i < args.length; i++) {
      MethodParameter methodParam = new MethodParameter(methodInvocation.getMethod(), i);
      methodParam.initParameterNameDiscovery(parameterNameDiscoverer);
      context.put(methodParam.getParameterName(), J.toJson(args[i]));
    }
    return context;
  }
}
