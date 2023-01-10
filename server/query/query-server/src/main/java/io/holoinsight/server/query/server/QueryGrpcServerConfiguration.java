/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.server;

import io.holoinsight.server.query.server.rpc.QueryGrpcService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2022/12/1
 *
 * @author xiangwanpeng
 */
@Configuration
public class QueryGrpcServerConfiguration {
  @Bean
  public QueryGrpcService queryGrpcService() {
    return new QueryGrpcService();
  }

  @Bean
  public QueryGrpcServer queryGrpcServer() {
    return new QueryGrpcServer();
  }
}
