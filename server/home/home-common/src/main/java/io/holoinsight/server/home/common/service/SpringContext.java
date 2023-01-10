/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 *
 * @author jsy1001de
 * @version 1.0: SpringContext.java, v 0.1 2022年02月24日 7:37 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service("SpringContext")
public class SpringContext implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext ac) throws BeansException {
    applicationContext = ac;
    log.info("begin init spring context.");
  }

  public static Object getBeanByName(String beanName) {
    if (applicationContext == null) {
      return null;
    }
    boolean contains = applicationContext.containsBean(beanName);
    if (!contains) {
      log.error("bean not exist in spring context? " + beanName);
      return null;
    }
    return applicationContext.getBean(beanName);
  }

  public static ApplicationContext getContext() {
    return applicationContext;
  }

  // 获取Bean
  public static <T> T getBean(Class<T> requiredType) {
    return (T) getContext().getBean(requiredType);
  }
}
