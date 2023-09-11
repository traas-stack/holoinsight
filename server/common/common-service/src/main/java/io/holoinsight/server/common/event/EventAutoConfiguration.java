/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * created at 2023/9/11
 *
 * @author xzchaoo
 */
public class EventAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public EventService eventService() {
    return new NoopEventService();
  }
}
