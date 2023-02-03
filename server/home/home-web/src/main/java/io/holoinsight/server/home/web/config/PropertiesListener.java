/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import io.holoinsight.server.home.common.util.CipherUtils;
import io.holoinsight.server.home.common.util.scope.MonitorEnv;
import lombok.extern.slf4j.Slf4j;

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
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent e) {

    ConfigurableEnvironment environment = e.getEnvironment();
    CipherUtils.setSeed(environment.getRequiredProperty("crypto.client.key"));

    String str = environment.getProperty("monitor.env");
    if (StringUtils.isNotEmpty(str)) {
      MonitorEnv.setCurrentEnv(str);
    }
  }
}
