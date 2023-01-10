/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import java.util.Arrays;

import io.holoinsight.server.home.common.service.PropertiesListenerConfig;
import io.holoinsight.server.home.common.util.scope.MonitorEnv;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author jsy1001de
 * @version 1.0: PropertiesListener.java, v 0.1 2022年02月25日 10:10 上午 jinsong.yjs Exp $
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Slf4j
@Deprecated
public class PropertiesListener
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(
      ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
    // 只同步必要的变量
    ConfigurableEnvironment environment = applicationEnvironmentPreparedEvent.getEnvironment();
    for (String k : Arrays.asList("crypto.client.key")) {
      PropertiesListenerConfig.getAllProperty().put(k, environment.getProperty(k));
    }

    String str = environment.getProperty("monitor.env");
    if (StringUtils.isNotEmpty(str)) {
      MonitorEnv.setCurrentEnv(str);
    }

    //
    // if (allProperty.containsKey("logging.path")) {
    // MDC.put("log_path", allProperty.get("logging.path"));
    // }

    // 读取配置环境
    // String env = null;
    // environment.getActiveProfiles();
    // if ((environment.getActiveProfiles().length > 0) && StringUtils.hasText(
    // environment.getActiveProfiles()[0])) {
    // env = environment.getActiveProfiles()[0];
    // }
    //
    // //applicationEnvironmentPreparedEvent.getEnvironment().getProperty("")
    //
    // if (StringUtil.isNotBlank(env)) {
    // PropertiesListenerConfig
    // .loadAllProperties(String.format("config/application-%s.properties", env));
    // } else {
    // PropertiesListenerConfig.loadAllProperties("config/application.properties");
    // }
    // Map<String, String> allProperty = PropertiesListenerConfig.getAllProperty();
    // log.info("property map: {}", allProperty);
    // if (CollectionUtils.isEmpty(allProperty)) {
    // return;
    // }
    //
    // if (allProperty.containsKey("monitor.env")) {
    // MonitorEnv.setCurrentEnv(allProperty.get("monitor.env"));
    // }
    //
    // if (allProperty.containsKey("logging.path")) {
    // MDC.put("log_path", allProperty.get("logging.path"));
    // }
  }
}
