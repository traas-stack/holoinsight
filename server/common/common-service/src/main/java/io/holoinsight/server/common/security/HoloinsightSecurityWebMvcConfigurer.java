/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * created at 2022/11/11
 *
 * @author xzchaoo
 */
public class HoloinsightSecurityWebMvcConfigurer implements WebMvcConfigurer {
  @Autowired
  private InternalWebApiInterceptor internalWebApiInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(internalWebApiInterceptor);
  }
}
