/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-12-28 19:07:00
 */
@Data
public class LevelAuthorizationMetaData implements SecurityMetaData {
  private List<String> parameters = new ArrayList<String>();

  /**
   * 解析后的上下文参数列表 如果是配置的单个类型的上下文，列表里面就是单个值 如果是session类型的配置的多个的上下文，就是多个值 也有可能是用户自定义的方式获取的
   */
  private List<String> contentValueList = new ArrayList<String>();

  /**
   * 检测手法，支持Equal、Contains，默认值是Equal
   */
  private String checkMode;

  /**
   * 获取待检测目标值的业务自定义类名
   */
  private String checkTargetCollectClass;

  /**
   * 待检测目标值列表
   */
  private List<String> checkTargetList = new ArrayList<String>();

  /**
   * 检测类型，支持ParameterCheck、TargetCheck、CustomCheck，默认值是CustomCheck
   */
  private String checkType;

  /**
   * 决策模式，支持ALLMATCH、EACHMATCH，默认值是ALLMATCH
   */
  private String decisionMode;

  /**
   * 函数的expression.name的记录，用于打印日志
   */
  private String methodString;

  /**
   * 用户自定义的水平权限校验方法的类名
   */
  private String levelAuthorizationCheckeClass;

  /**
   * 拦截器位置，支持Before、After，默认值是Before
   */
  private String interceptorSeat;
}
