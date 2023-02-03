/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.bootstrap;

import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import io.holoinsight.server.extension.ceresdbx.HoloinsightCeresdbxConfiguration;
import io.holoinsight.server.query.service.QueryService;
import io.holoinsight.server.query.service.impl.DefaultQueryServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xiangwanpeng
 */
@ComponentScan({"io.holoinsight.server.query"})
@EnableScheduling
@MapperScan(basePackages = {"io.holoinsight.server.query.dal.mapper",
    "io.holoinsight.server.common.dao.mapper"})
@ConditionalOnRole("query")
@Import({ThreadPoolConfiguration.class, HoloinsightCeresdbxConfiguration.class})
public class HoloinsightQueryConfiguration {
  @Bean
  public QueryService queryService() {
    return new DefaultQueryServiceImpl();
  }
}
