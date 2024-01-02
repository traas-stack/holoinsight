/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-01-02 10:45:00
 */
public interface CheckTargetCollector {

  /**
   * 提供给业务自定义使用的通用待校验目标获取方法 获取单个待校验的目标
   * 
   * @param paramters
   * @param methodInvocation
   * @return
   */
  public String getCheckTarget(List<String> paramters, MethodInvocation methodInvocation);

  /**
   * 提供给业务自定义使用的通用待校验目标获取方法 获取待校验目标的list
   * 
   * @param paramters
   * @param methodInvocation
   * @return
   */
  public List<String> getCheckTargetList(List<String> paramters, MethodInvocation methodInvocation);
}
