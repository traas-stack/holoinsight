/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import io.holoinsight.server.home.web.interceptor.MonitorScopeAuthInterceptor;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import io.holoinsight.server.home.web.security.LevelAuthorizationInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: WebMvcConfig.java, v 0.1 2022年02月22日 7:16 下午 jinsong.yjs Exp $
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  // @Bean
  // public FilterRegistrationBean testFilterRegistration() {
  // FilterRegistrationBean registration = new FilterRegistrationBean();
  //
  // String exclusions = "*.css,*.png,*.gif,*.png,*.jpg,*.js,*.html,/api/*,";
  // registration.addInitParameter("exclusions", exclusions);
  // registration.setName("ssoFilter");
  // registration.setOrder(1);
  // return registration;
  // }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // String staticPath = "file:/home/admin/cloudmonitor/cloudmonitor-front/";
    // registry.addResourceHandler("/**")
    // .addResourceLocations(staticPath, "classpath:/META-INF/resources/", "classpath:/resources/",
    // "classpath:/static/", "classpath:/public/").setCachePeriod(60);
    // registry.addResourceHandler("swagger-ui.html");
  }

  @Bean
  public DefaultPointcutAdvisor defaultPointcutAdvisor2() {

    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
    advisor.setAdvice(new MonitorScopeAuthInterceptor());

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut
        .setExpression("@annotation(io.holoinsight.server.home.web.interceptor.MonitorScopeAuth)");
    advisor.setPointcut(pointcut);
    return advisor;
  }

  @Bean("levelAuthPointcutAdvisor")
  public DefaultPointcutAdvisor defaultPointcutAdvisor3() {

    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(
        AnnotationMatchingPointcut.forMethodAnnotation(LevelAuthorizationAccess.class),
        levelAuthorizationInterceptor());
    return advisor;
  }

  @Bean
  public LevelAuthorizationInterceptor levelAuthorizationInterceptor() {
    return new LevelAuthorizationInterceptor();
  }

  @Bean
  public HttpMessageConverter<String> responseBodyStringConverter() {
    return new StringHttpMessageConverter(StandardCharsets.UTF_8);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(responseBodyStringConverter());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(new RefererInterceptor());
    // registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");
  }
}
