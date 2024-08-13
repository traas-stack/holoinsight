/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author masaimu
 * @version 2023-12-28 18:06:00
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LevelAuthorizationAccess {

  /**
   * 待校验参数配置信息
   * 
   * @return
   */
  public String[] paramConfigs() default "";

  /**
   * 获取上下文的容器，默认值是CUSTOM
   * 
   * @return
   */
  public String contentContainer() default "CUSTOM";

  /**
   * 获取上下文的key，默认值是空白，配套CUSTOM
   * 
   * @return
   */
  public String contentKey() default "";

  /**
   * 校验方式，目前支持equal、contains
   * 
   * @return
   */
  public String checkMode() default "Equal";

  /**
   * 获取用户自定义校验目标的类名
   * 
   * @return
   */
  public String checkTargetCollectClass() default "";

  /**
   * 获取用户自定义上下文目标的类名
   * 
   * @return
   */
  public String contentTargetCollectClass() default "";

  /**
   * 校验类型，目前支持3种 ParameterCheck，参数校验 TargetCheck，目标对象校验 CustomCheck，自定义校验模式，默认设置
   * 
   * @return
   */
  public String checkType() default "CustomCheck";

  /**
   * 决策模式，目前支持2种 ALLMATCH，全部匹配模式，默认设置 EACHMATCH，任意一条匹配模式
   * 
   * @return
   */
  public String decisionMode() default "ALLMATCH";

  /**
   * 用户自定义的水平权限校验方法的类名
   * 
   * @return
   */
  public String levelAuthorizationCheckeClass() default "";

  /**
   * 拦截器所属位置，目前支持2种 Before，前置拦截，默认设置 After，后置拦截
   * 
   * @return
   */
  public String interceptorSeat() default "Before";
}
