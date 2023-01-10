/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.common;

import io.holoinsight.server.meta.common.integration.dict.PropertiesListenerConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: PropertiesListener.java, v 0.1 2022年02月25日 10:10 上午 jinsong.yjs Exp $
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 11)
public class PropertiesListener
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(
      ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
    // 读取配置环境
    String env = null;
    if (!ArrayUtils
        .isEmpty(applicationEnvironmentPreparedEvent.getEnvironment().getActiveProfiles())
        && StringUtils
            .hasText(applicationEnvironmentPreparedEvent.getEnvironment().getActiveProfiles()[0])) {
      env = applicationEnvironmentPreparedEvent.getEnvironment().getActiveProfiles()[0];
    }

    if (!StringUtils.isEmpty(env)) {
      PropertiesListenerConfig
          .loadAllProperties(String.format("config/application-%s.properties", env));
    } else {
      PropertiesListenerConfig.loadAllProperties("config/application.yaml");
    }
    Map<String, String> allProperty = PropertiesListenerConfig.getAllProperty();
    if (CollectionUtils.isEmpty(allProperty)) {
      return;
    }

    if (allProperty.containsKey("logging.path")) {
      MDC.put("log_path", allProperty.get("logging.path"));
    }
  }
}
