/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.DateFormat;

/**
 *
 * @author jsy1001de
 * @version 1.0: JacksonConfig.java, v 0.1 2022年12月08日 下午5:19 jinsong.yjs Exp $
 */
@Configuration
public class JacksonConfig {
  @Autowired
  private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

  @Bean
  @ConditionalOnMissingBean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

    ObjectMapper mapper = jackson2ObjectMapperBuilder.build();

    DateFormat dateFormat = mapper.getDateFormat();
    mapper.setDateFormat(new MonitorDateFormat(dateFormat));

    MappingJackson2HttpMessageConverter mappingJsonpHttpMessageConverter =
        new MappingJackson2HttpMessageConverter(mapper);
    return mappingJsonpHttpMessageConverter;
  }

}
