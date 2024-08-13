/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static io.holoinsight.server.home.web.security.LevelAuthorizationDecisionManager.formatError;

/**
 * @author masaimu
 * @version 2023-12-28 18:17:00
 */
@Slf4j
@Service
public class LevelAuthorizationMetadataSource {

  private List<CheckTargetCollector> checkTargetCollectorList =
      new ArrayList<CheckTargetCollector>();
  private List<ContentTargetCollector> contentTargetCollectorList =
      new ArrayList<ContentTargetCollector>();
  private ParameterNameDiscoverer parameterNameDiscoverer =
      new LocalVariableTableParameterNameDiscoverer();


  public List<SecurityMetaData> getMetaDataList(MethodInvocation methodInvocation)
      throws IllegalArgumentException {
    List<SecurityMetaData> configAttributes = new ArrayList<SecurityMetaData>();
    // 开始解析水平权限注解，构造检测元数据
    LevelAuthorizationAccess levelAuthAccess = AnnotationUtils
        .findAnnotation(methodInvocation.getMethod(), LevelAuthorizationAccess.class);
    if (levelAuthAccess != null) {
      LevelAuthorizationMetaData levelAuthMetaData = new LevelAuthorizationMetaData();
      // 从注解中获取一些己初配置填充到元数据里面
      levelAuthMetaData.setInterceptorSeat(levelAuthAccess.interceptorSeat());
      levelAuthMetaData.setCheckMode(levelAuthAccess.checkMode());
      levelAuthMetaData.setCheckType(levelAuthAccess.checkType());
      levelAuthMetaData.setDecisionMode(levelAuthAccess.decisionMode());
      levelAuthMetaData.setCheckTargetCollectClass(levelAuthAccess.checkTargetCollectClass());
      levelAuthMetaData
          .setLevelAuthorizationCheckeClass(levelAuthAccess.levelAuthorizationCheckeClass());
      // 解析参数，如果有获取HttpServletRequest参数
      HttpServletRequest request = ParameterParseUtil.getServletRequest(methodInvocation);
      if (null == request) {
        request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      }

      // 解析上下文获取逻辑，获取上下文
      String contentContainer = levelAuthAccess.contentContainer();
      String contentKey = levelAuthAccess.contentKey();
      parseContentValue(contentContainer, contentKey, request, levelAuthMetaData, levelAuthAccess,
          methodInvocation);
      if (StringUtils.equals(levelAuthAccess.interceptorSeat(), "Before")) {
        // 遍历获取params
        printReqParamNames(request);
        List<String> parameters = parseParameter(methodInvocation, levelAuthAccess.paramConfigs(),
            request, this.parameterNameDiscoverer);
        levelAuthMetaData.setParameters(parameters);
        // 解析获取待校验目标列表
        parseCheckTargetList(levelAuthMetaData, levelAuthAccess, methodInvocation);
      }
      // 将水平权限元数据添加到元数据列表
      configAttributes.add(levelAuthMetaData);
    }
    return configAttributes;
  }

  private void printReqParamNames(HttpServletRequest request) {
    Enumeration<String> names = request.getParameterNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      log.info("printReqParamNames {}", name);
    }
  }

  /**
   * 解析上下文的值
   * 
   * @param contentContainer
   * @param contentKey
   * @param request
   * @param levelAuthMetaData
   * @param levelAuthorizationAccess
   * @param methodInvocation
   */
  private void parseContentValue(String contentContainer, String contentKey,
      HttpServletRequest request, LevelAuthorizationMetaData levelAuthMetaData,
      LevelAuthorizationAccess levelAuthorizationAccess, MethodInvocation methodInvocation) {
    if (StringUtils.equals(contentContainer, "CUSTOM")) {
      if (StringUtils.isNotBlank(levelAuthorizationAccess.contentTargetCollectClass())) {
        for (ContentTargetCollector contentTargetCollector : contentTargetCollectorList) {
          if (contentTargetCollector == null || contentTargetCollector.getClass() == null
              || contentTargetCollector.getClass().getName() == null) {
            continue;
          }
          if (StringUtils.equals(contentTargetCollector.getClass().getName(),
              levelAuthorizationAccess.contentTargetCollectClass())) {
            // 获取待检测目标list和获取待检测目标的方法都执行一遍，结果合并到一个最终list里面进行检测
            List<String> contentValueList = contentTargetCollector
                .getContentTargetList(levelAuthMetaData.getParameters(), methodInvocation);
            if (contentValueList == null) {
              contentValueList = new ArrayList<String>();
            }
            String singleCheckTarget = contentTargetCollector
                .getContentTarget(levelAuthMetaData.getParameters(), methodInvocation);
            if (StringUtils.isNotBlank(singleCheckTarget)) {
              contentValueList.add(singleCheckTarget);
            }
            levelAuthMetaData.setContentValueList(contentValueList);
            return;
          }
        }
      }
    } else {
      // 配置出错了，没有获取到可用的上下文提取容器
      formatError(log, "LevelAuthorizationMetadataSource", "parseContentValue",
          "ContainerNotSupport", "pass=false",
          "Unknow content container!Config error, need to be blocked!");
      throw new LevelAuthorizationCheckException("content config error!");
    }
  }

  /**
   * 解析参数
   * 
   * @param methodInvocation
   * @param configs
   * @param request
   * @param parameterNameDiscoverer
   * @return
   */
  private List<String> parseParameter(MethodInvocation methodInvocation, String[] configs,
      HttpServletRequest request, ParameterNameDiscoverer parameterNameDiscoverer) {
    List<String> parameters = new ArrayList<String>();
    for (String config : configs) {
      String[] splits = StringUtils.split(config, ":");
      if (splits.length != 2) {
        // 这里应该抛异常
        formatError(log, "LevelAuthorizationMetadataSource", "parseParameter", "ParamConfigError",
            "pass=false##config=" + config,
            "Single parameter config contains 2 parts!This config is invailded!need to be blocked!");
        throw new LevelAuthorizationCheckException("param config error!");
      }
      String paramType = splits[0];
      String parameterName = splits[1];
      if (StringUtils.equals(paramType, "REQUEST")) {
        if (request == null) {
          formatError(log, "LevelAuthorizationMetadataSource", "parseParameter", "RequestIsNull",
              "pass=false",
              "Config is get patameter from httpserveltrequest, but request is null!Config is wrong, need to be blocked!");
          throw new LevelAuthorizationCheckException("param config error!");
        }
        String parameter = request.getParameter(parameterName);
        if (StringUtils.isBlank(parameter)) {
          formatError(log, "LevelAuthorizationMetadataSource", "parseParameter",
              "RequestParameterIsNull", "pass=false",
              "Patameter get from httpserveltrequest is blank!Config is wrong, need to be blocked!");
          throw new LevelAuthorizationCheckException("param config error!");
        }
        parameters.add(parameter);
      } else if (StringUtils.equals(paramType, "PARAMETER")) {
        String parameter = ParameterParseUtil.getParameterFromArguments(methodInvocation,
            parameterName, parameterNameDiscoverer);
        log.info("parseParameter PARAMETER {} {}", parameterName, parameter);
        if (StringUtils.isBlank(parameter)) {
          formatError(log, "LevelAuthorizationMetadataSource", "parseParameter", "ParameterIsNull",
              "pass=false",
              "Patameter get from arguments is blank!Config is wrong, need to be blocked!");
          throw new LevelAuthorizationCheckException("param config error!");
        }
        parameters.add(parameter);
      } else {
        // 这里应该抛异常
        formatError(log, "LevelAuthorizationMetadataSource", "parseParameter", "ParameterIsNull",
            "pass=false", "Get parameter catch Exception!Config is wrong, need to be blocked!");
        throw new LevelAuthorizationCheckException("param config error!");
      }
    }
    return parameters;
  }

  /**
   * 解析checkTarget对象
   * 
   * @param levelAuthMetaData
   * @param levelAuthAccess
   * @param methodInvocation
   */
  private void parseCheckTargetList(LevelAuthorizationMetaData levelAuthMetaData,
      LevelAuthorizationAccess levelAuthAccess, MethodInvocation methodInvocation) {
    if (StringUtils.equals(levelAuthMetaData.getCheckType(), CheckTypeEnum.PARAMETERCHECK)) {
      // 如果是参数检查模式，把参数列表赋值给checkTarget列表，checkTarget列表统一作为检测目标对象存在
      levelAuthMetaData.setCheckTargetList(levelAuthMetaData.getParameters());
    } else if (StringUtils.equals(levelAuthMetaData.getCheckType(), CheckTypeEnum.TARGETCHECK)) {
      // 如果观察当前注解有没有配置先查后检模式的待检变量采集器
      if (StringUtils.isNotBlank(levelAuthAccess.checkTargetCollectClass())) {
        for (CheckTargetCollector checkTargetCollector : checkTargetCollectorList) {
          if (checkTargetCollector == null || checkTargetCollector.getClass() == null
              || checkTargetCollector.getClass().getName() == null) {
            continue;
          }
          if (StringUtils.equals(checkTargetCollector.getClass().getName(),
              levelAuthAccess.checkTargetCollectClass())) {
            // 获取待检测目标list和获取待检测目标的方法都执行一遍，结果合并到一个最终list里面进行检测
            List<String> checkTargetList = checkTargetCollector
                .getCheckTargetList(levelAuthMetaData.getParameters(), methodInvocation);
            if (checkTargetList == null) {
              checkTargetList = new ArrayList<String>();
            }
            String singleCheckTarget = checkTargetCollector
                .getCheckTarget(levelAuthMetaData.getParameters(), methodInvocation);
            if (StringUtils.isNotBlank(singleCheckTarget)) {
              checkTargetList.add(singleCheckTarget);
            }
            levelAuthMetaData.setCheckTargetList(checkTargetList);
            return;
          }
        }
      }
    }
  }

}
