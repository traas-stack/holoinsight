/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author xiangwanpeng
 */
@Configuration
public class QueryApiConfig {

  @Bean
  @ConditionalOnMissingBean
  public ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufJsonFormatHttpMessageConverter();
  }

}
