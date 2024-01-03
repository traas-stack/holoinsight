/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-12-28 19:04:00
 */
@Slf4j
@Service
public class LevelAuthorizationDecisionManager {

  private List<LevelAuthorizationCheck> levelAuthorizationChecks;

  @Autowired
  public void setLevelAuthorizationChecks(List<LevelAuthorizationCheck> levelAuthorizationChecks) {
    this.levelAuthorizationChecks = levelAuthorizationChecks;
  }

  public void beforeDecide(List<SecurityMetaData> securityMetaDataList,
      MethodInvocation methodInvocation) throws LevelAuthorizationCheckException {
    if (securityMetaDataList == null) {
      // 抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "beforeDecide",
          "SecurityMetaDataListIsNull", "pass=false",
          "SecurityMetaData is null!Config is wrong, need to be blocked!");
      throw new LevelAuthorizationCheckException("security check fail!");
    }
    if (securityMetaDataList.size() < 1) {
      // 抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "beforeDecide",
          "SecurityMetaDataListIsNull", "pass=false",
          "SecurityMetaData list is empty!Config is wrong, need to be blocked!");
      throw new LevelAuthorizationCheckException("security check fail!");
    }
    for (SecurityMetaData securityMetaData : securityMetaDataList) {
      if (securityMetaData instanceof LevelAuthorizationMetaData) {
        LevelAuthorizationMetaData levelAuthMetaData =
            (LevelAuthorizationMetaData) securityMetaData;
        // 如果配置元数据中配置的拦截位置不是前置校验，则前置校验决策模块直接跳过，不执行决策逻辑
        if (!StringUtils.equals(levelAuthMetaData.getInterceptorSeat(), "Before")) {
          continue;
        }
        singleDecide(levelAuthMetaData, methodInvocation);
      }
    }
  }

  public void afterDecide(List<SecurityMetaData> securityMetaDataList,
      MethodInvocation methodInvocation, Object result) throws LevelAuthorizationCheckException {
    if (securityMetaDataList == null) {
      // 抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "afterDecide",
          "SecurityMetaDataListIsNull", "pass=false",
          "SecurityMetaData is null!Config is wrong, need to be blocked!");
      throw new LevelAuthorizationCheckException("security check fail!");
    }
    if (securityMetaDataList.size() < 1) {
      // 抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "afterDecide",
          "SecurityMetaDataListIsNull", "pass=false",
          "SecurityMetaData list is empty!Config is wrong, need to be blocked!");
      throw new LevelAuthorizationCheckException("security check fail!");
    }
    // 根据后置校验对象的注解情况，来获取待校验的目标字符串
    List checkTargetList = getCheckTargetList(result);
    for (SecurityMetaData securityMetaData : securityMetaDataList) {
      if (securityMetaData instanceof LevelAuthorizationMetaData) {
        LevelAuthorizationMetaData levelAuthMetaData =
            (LevelAuthorizationMetaData) securityMetaData;
        levelAuthMetaData.setCheckTargetList(checkTargetList);
        // 如果配置元数据中配置的拦截位置不是后置校验，则后置校验决策模块直接跳过，不执行决策逻辑
        if (!StringUtils.equals(levelAuthMetaData.getInterceptorSeat(), "After")) {
          continue;
        }
        singleDecide(levelAuthMetaData, methodInvocation);
      }
    }
  }

  /**
   * 单条元数据的决策逻辑执行
   * 
   * @param levelAuthMetaData
   * @param methodInvocation
   * @throws LevelAuthorizationCheckException
   */
  public void singleDecide(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) throws LevelAuthorizationCheckException {
    // 校验模式必须配置，不存在配置水平权限注解但是不配置任何一种校验模式而通过校验的情况，发现这个配置为空进行拦截
    if (StringUtils.isBlank(levelAuthMetaData.getCheckType())) {
      // 抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "decide", "CheckTypeIsBlank",
          "pass=false", "CheckType is null!Config is wrong, need to be blocked!");
      throw new LevelAuthorizationCheckException("level auth check failed!");
    }
    if (StringUtils.equals(levelAuthMetaData.getCheckType(), "CustomCheck")) {
      if (null == levelAuthorizationChecks || levelAuthorizationChecks.isEmpty()) {
        // 配置了自定义水平权限校验模式，但是却没有配置自定义的水平权限校验类名，配置异常
        formatError(log, "LevelAuthorizationDecisionManager", "decide", "CustomCheckerIsBlank",
            "pass=false",
            "Custom levelauthorization checks is null or empty!Config is wrong, need to be blocked!");
        throw new LevelAuthorizationCheckException("security check failed!");
      }
      if (StringUtils.isBlank(levelAuthMetaData.getLevelAuthorizationCheckeClass())) {
        // 配置了自定义水平权限校验模式，但是却没有配置自定义的水平权限校验类名，配置异常
        formatError(log, "LevelAuthorizationDecisionManager", "decide", "CustomCheckerIsBlank",
            "pass=false",
            "Custom levelauthorization check class is blank!Config is wrong, need to be blocked!");
        throw new LevelAuthorizationCheckException("security check failed!");
      }
      for (LevelAuthorizationCheck levelAuthorizationCheck : levelAuthorizationChecks) {
        if (levelAuthorizationCheck == null || levelAuthorizationCheck.getClass() == null
            || StringUtils.isEmpty(levelAuthorizationCheck.getClass().getName())) {
          // 配置了自定义水平权限校验模式，但是却没有配置自定义的水平权限校验类名，配置异常
          formatError(log, "LevelAuthorizationDecisionManager", "decide", "CustomCheckerNotVailed",
              "pass=false",
              "Custom levelauthorization checker is null!Config is wrong, need to be blocked!");
          throw new LevelAuthorizationCheckException("security check failed!");
        }
        if (StringUtils.equals(levelAuthorizationCheck.getClass().getName(),
            levelAuthMetaData.getLevelAuthorizationCheckeClass())) {
          if (!levelAuthorizationCheck.check(levelAuthMetaData, methodInvocation)) {
            // 抛异常
            formatError(log, "LevelAuthorizationDecisionManager", "decide", "SecurityDecideFailed",
                "pass=false", "Custom levelauthorization checker find risk, need to be blocked!");
            throw new LevelAuthorizationCheckException("security check failed!");
          }
        }
      }
    } else {
      // 水平权限校验不通过，抛异常
      formatError(log, "LevelAuthorizationDecisionManager", "decide", "SecurityDecideFailed",
          "pass=false", "Cannot find levelauthorization check type, need to be blocked!");
      throw new LevelAuthorizationCheckException(
          "security check failed! Cannot find levelauthorization check type.");
    }
  }

  /**
   * 从结果对象里面根据注解获取检测目标
   * 
   * @param result
   * @return
   */
  public List<String> getCheckTargetList(Object result) {
    List<String> checkTargetList = new ArrayList<String>();
    if (null == result) {
      return checkTargetList;
    }
    Method[] methods = result.getClass().getMethods();
    if (null == methods || methods.length < 1) {
      // 抛异常

      throw new LevelAuthorizationCheckException("security check fail!");
    }
    for (Method method : methods) {
      if (AnnotationUtils.findAnnotation(method, LevelAuthContentGet.class) != null) {
        try {
          Object target = method.invoke(result);
          if (null != target && target instanceof String) {
            checkTargetList.add((String) target);
          }
        } catch (IllegalAccessException e) {
          formatError(log, "LevelAuthorizationDecisionManager", "getCheckTargetList",
              "GetCheckTargetListException", "pass=false",
              "Get check target list catch exception!Level auth check target can't be blank, need to be blocked!");
          throw new LevelAuthorizationCheckException("security check fail!");
        } catch (InvocationTargetException e) {
          formatError(log, "LevelAuthorizationDecisionManager", "getCheckTargetList",
              "GetCheckTargetListException", "pass=false",
              "Get check target list catch exception!Level auth check target can't be blank, need to be blocked!");
          throw new LevelAuthorizationCheckException("security check fail!");
        }
      }
    }
    return checkTargetList;
  }

  public static void formatError(Logger logger, String clazzName, String methodName, String logKey,
      String kvString, String desc) {
    String logMessage =
        String.format("[%s-%s|%s|%s|%s]", clazzName, methodName, logKey, kvString, desc);
    logger.error(logMessage);
  }
}
