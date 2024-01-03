/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-01-02 10:34:00
 */
public interface ContentTargetCollector {

  /**
   * 提供给业务自定义使用的通用上下文目标获取方法 获取单个待校验的目标
   * 
   * @param parameters
   * @param methodInvocation
   * @return
   */
  public String getContentTarget(List<String> parameters, MethodInvocation methodInvocation);

  /**
   * 提供给业务自定义使用的通用上下文目标获取方法 获取待校验目标的list
   * 
   * @param parameters
   * @param methodInvocation
   * @return
   */
  public List<String> getContentTargetList(List<String> parameters,
      MethodInvocation methodInvocation);
}
