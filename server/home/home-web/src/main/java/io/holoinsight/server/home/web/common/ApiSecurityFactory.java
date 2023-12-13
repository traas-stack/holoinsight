/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.facade.ApiSecurity;
import io.holoinsight.server.home.facade.utils.SecurityMethodCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-11-21 20:06:00
 */
@Slf4j
@Component
public class ApiSecurityFactory implements BeanPostProcessor {

  public static final Map<String /* method full name */, List<String /* param name */>> createParameterMap =
      new HashMap<>();
  public static final Map<String /* method full name */, List<String /* param name */>> updateParameterMap =
      new HashMap<>();
  public static final Map<String /* method full name */, List<String /* param name */>> readParameterMap =
      new HashMap<>();


  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    final Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
    RestController restController = beanClass.getAnnotation(RestController.class);
    if (restController != null) {
      parseController(beanClass);
    }
    return bean;
  }

  private void parseController(Class<?> beanClass) {
    RequestMapping requestMapping = beanClass.getAnnotation(RequestMapping.class);
    if (requestMapping == null) {
      return;
    }

    String className = beanClass.getName();

    Method[] methods = beanClass.getDeclaredMethods();
    for (Method method : methods) {
      PostMapping postMapping = method.getAnnotation(PostMapping.class);
      if (postMapping == null) {
        continue;
      }
      String methodName = method.getName();
      String key = getFullMethodName(beanClass, method);
      Parameter[] params = method.getParameters();
      for (Parameter parameter : params) {
        SecurityResource securityResource = parameter.getAnnotation(SecurityResource.class);
        if (securityResource == null) {
          continue;
        }
        if (!ApiSecurity.class.isAssignableFrom(parameter.getType())) {
          log.info("{} {} {} is not ApiSecurity", className, methodName, parameter.getName());
          continue;
        }
        SecurityMethodCategory m = securityResource.value();
        switch (m) {
          case create:
            log.info("ApiSecurityFactory init, add {} to createParameterMap", key);
            List<String /* param name */> cl =
                createParameterMap.computeIfAbsent(key, k -> new ArrayList<>());
            cl.add(parameter.getName());
            break;
          case update:
            log.info("ApiSecurityFactory init, add {} to updateParameterMap", key);
            List<String /* param name */> ul =
                updateParameterMap.computeIfAbsent(key, k -> new ArrayList<>());
            ul.add(parameter.getName());
            break;
          case query:
            log.info("ApiSecurityFactory init, add {} to readParameterMap", key);
            List<String /* param name */> rl =
                readParameterMap.computeIfAbsent(key, k -> new ArrayList<>());
            rl.add(parameter.getName());
            break;
        }
      }

    }
  }

  public static String getFullMethodName(Class<?> beanClass, Method method) {
    String className = beanClass.getName();
    String methodName = method.getName();
    StringBuilder fullMethodName = new StringBuilder(className).append(".").append(methodName);
    List<String> paraNames = new ArrayList<>();

    Class<?>[] parameterTypes = method.getParameterTypes();
    for (Class<?> paramType : parameterTypes) {
      paraNames.add(paramType.getName());
    }
    fullMethodName.append("(").append(String.join(",", paraNames)).append(")");
    return fullMethodName.toString();
  }
}
