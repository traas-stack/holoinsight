/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import com.google.protobuf.util.JsonFormat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class HomeApiConfig {

  @Bean
  @ConditionalOnMissingBean
  public ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufJsonFormatHttpMessageConverter(JsonFormat.parser().ignoringUnknownFields(),
        null);
  }

}
