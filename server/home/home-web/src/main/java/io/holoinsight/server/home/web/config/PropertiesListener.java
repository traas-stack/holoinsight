/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import io.holoinsight.server.home.common.util.CipherUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jsy1001de
 * @version 1.0: PropertiesListener.java, v 0.1 2022年02月25日 10:10 上午 jinsong.yjs Exp $
 */
// This listener depends on EnvironmentPostProcessorApplicationListener
@Order(EnvironmentPostProcessorApplicationListener.DEFAULT_ORDER + 10)
@Slf4j
public class PropertiesListener
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent e) {
    ConfigurableEnvironment environment = e.getEnvironment();
    CipherUtils.setSeed(environment.getRequiredProperty("crypto.client.key"));
  }
}
