/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-12-28 18:16:00
 */
@Slf4j
public class LevelAuthorizationInterceptor implements MethodInterceptor {

  @Autowired
  private LevelAuthorizationMetadataSource levelAuthorizationMetadataSource;
  @Autowired
  private LevelAuthorizationDecisionManager levelAuthorizationDecisionManager;

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    Object result;
    try {
      log.info("LevelAuthorizationInterceptor invoke");

      List<SecurityMetaData> securityMetaDataList =
          levelAuthorizationMetadataSource.getMetaDataList(methodInvocation);
      beforeInvocation(securityMetaDataList, methodInvocation);
      result = methodInvocation.proceed();
      afterInvocation(securityMetaDataList, methodInvocation, result);
      return result;
    } catch (LevelAuthorizationCheckException e) {
      log.error("fail to invoke LevelAuthorizationInterceptor ", e);
      throw e;
    }
  }

  private void beforeInvocation(List<SecurityMetaData> securityMetaDataList,
      MethodInvocation methodInvocation) {
    levelAuthorizationDecisionManager.beforeDecide(securityMetaDataList, methodInvocation);
  }

  private void afterInvocation(List<SecurityMetaData> securityMetaDataList,
      MethodInvocation methodInvocation, Object result) {
    levelAuthorizationDecisionManager.afterDecide(securityMetaDataList, methodInvocation, result);
  }
}
