/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.hook;

import org.springframework.context.annotation.Bean;

/**
 * <p>
 * created at 2022/12/6
 *
 * @author xzchaoo
 */
public class CommonHooksAutoConfiguration {
  @Bean
  public CommonHooksManager commonHooksManager() {
    return new CommonHooksManager();
  }
}
