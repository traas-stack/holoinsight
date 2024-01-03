/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-01-02 11:29:00
 */
@Slf4j
@Service
public class AlarmRuleLevelAuthorizationChecker implements LevelAuthorizationCheck {
  @Override
  public boolean check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    log.info("AlarmRuleLevelAuthorizationChecker {} {}", methodName, parameters);
    return true;
  }
}
