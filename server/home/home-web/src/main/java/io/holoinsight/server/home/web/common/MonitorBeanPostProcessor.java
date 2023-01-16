/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.biz.plugin.PluginRepository;
import io.holoinsight.server.home.biz.plugin.model.Plugin;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.task.AbstractMonitorTask;
import io.holoinsight.server.home.task.TaskFactoryHolder;
import io.holoinsight.server.home.task.TaskHandler;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorBeanPostProcessor.java, v 0.1 2022年03月15日 8:23 下午 jinsong.yjs Exp $
 */
@Component
public class MonitorBeanPostProcessor implements BeanPostProcessor {

  @Autowired
  private PluginRepository pluginRepository;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    final Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
    processTokenUrlScopeAnnotation(bean, beanClass);
    processExecutorHandlerAnnotation(bean, beanClass);
    processPluginModelAnnotation(bean, beanClass);
    return bean;
  }

  private void processPluginModelAnnotation(Object bean, Class<?> beanClass) {


    PluginModel pluginModel = beanClass.getAnnotation(PluginModel.class);
    if (pluginModel == null) {
      return;
    }
    if (!(bean instanceof Plugin)) {
      return;
    }
    this.pluginRepository.registry((Plugin) bean, pluginModel.name(), pluginModel.version());
  }

  private void processTokenUrlScopeAnnotation(Object bean, Class<?> beanClass) {

    TokenUrls tokenScopes = beanClass.getAnnotation(TokenUrls.class);
    if (null != tokenScopes) {
      for (String value : tokenScopes.value()) {
        TokenUrlFactoryHolder.setUrl(value);
      }
    }
  }

  private void processExecutorHandlerAnnotation(Object bean, Class<?> beanClass) {

    TaskHandler taskHandler = beanClass.getAnnotation(TaskHandler.class);
    if (taskHandler == null) {
      return;
    }
    TaskFactoryHolder.setExecutorTask(taskHandler.value(), (AbstractMonitorTask) bean);
  }

}
