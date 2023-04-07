/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.bootstrap;

import io.holoinsight.server.common.auth.ApiKeyAutoConfiguration;
import io.holoinsight.server.common.config.ConfigConfiguration;
import io.holoinsight.server.common.dao.mapper.GaeaCollectConfigDOMapper;
import io.holoinsight.server.common.groovy.GroovyConfiguration;
import io.holoinsight.server.common.security.InternalWebApiSecurityConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import io.holoinsight.server.registry.core.RegistryProperties;

import io.holoinsight.server.registry.core.meta.DefaultMetaWriterServiceImpl;
import io.holoinsight.server.registry.core.meta.MetaWriterService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author zzhb101
 */
@Configuration
@ConditionalOnRole("registry")
@ComponentScan(basePackages = {"io.holoinsight.server.registry", //
})
@EnableConfigurationProperties(RegistryProperties.class)
@EnableScheduling
@MapperScan(basePackageClasses = GaeaCollectConfigDOMapper.class)
@Import({ConfigConfiguration.class, GroovyConfiguration.class, ThreadPoolConfiguration.class,
    InternalWebApiSecurityConfiguration.class, ApiKeyAutoConfiguration.class})
public class HoloinsightRegistryConfiguration {

  @Bean
  public MetaWriterService metaWriterService() {
    return new DefaultMetaWriterServiceImpl();
  }
}
