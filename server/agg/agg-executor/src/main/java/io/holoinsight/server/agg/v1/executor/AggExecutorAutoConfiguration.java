/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

import javax.sql.DataSource;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.core.mapper.AggOffsetV1DOMapper;
import io.holoinsight.server.agg.v1.executor.executor.AggMetaService;
import io.holoinsight.server.agg.v1.executor.executor.AggMetaServiceImpl;
import io.holoinsight.server.agg.v1.executor.executor.ExecutorConfig;
import io.holoinsight.server.agg.v1.executor.executor.ExecutorManager;
import io.holoinsight.server.agg.v1.executor.executor.FaultToleranceConfig;
import io.holoinsight.server.agg.v1.executor.executor.StateConfig;
import io.holoinsight.server.agg.v1.executor.output.AsyncOutput;
import io.holoinsight.server.agg.v1.executor.output.XConsoleOutput;
import io.holoinsight.server.agg.v1.executor.output.XOutput;
import io.holoinsight.server.agg.v1.executor.service.AggTaskV1StorageForExecutor;
import io.holoinsight.server.agg.v1.executor.service.AggTaskV1Syncer;
import io.holoinsight.server.agg.v1.executor.service.IAggTaskService;
import io.holoinsight.server.agg.v1.executor.service.JdbcAggTaskService;
import io.holoinsight.server.agg.v1.executor.state.JdbcPartitionStateStore;
import io.holoinsight.server.agg.v1.executor.state.PartitionStateStore;
import io.holoinsight.server.common.config.ConfigConfiguration;
import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import io.holoinsight.server.meta.facade.service.DataClientService;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */

@Configuration
@EnableScheduling
@ConditionalOnRole("agg-executor")
@EnableConfigurationProperties(AggProperties.class)
@Import({CommonDaoConfiguration.class, ConfigConfiguration.class, ThreadPoolConfiguration.class})
@MapperScan(basePackageClasses = AggOffsetV1DOMapper.class)
public class AggExecutorAutoConfiguration {
  @Autowired
  private AggProperties aggProperties;

  @Bean
  public AggTaskV1StorageForExecutor aggTaskV1StorageForExecutor() {
    return new AggTaskV1StorageForExecutor();
  }

  @Bean
  public AggTaskV1Syncer aggTaskV1Syncer() {
    return new AggTaskV1Syncer();
  }

  @DependsOn("aggTaskV1Syncer")
  @Bean
  public IAggTaskService aggTaskService() {
    return new JdbcAggTaskService();
  }

  @Bean
  public PartitionStateStore stateStore() {
    return new JdbcPartitionStateStore();
  }

  @Bean
  public AsyncOutput asyncOutput() {
    return new AsyncOutput();
  }

  @Bean
  public ExecutorManager executorManager(DataSource dataSource, IAggTaskService aggTaskService,
      PartitionStateStore stateStore, CompletenessService completenessService, AsyncOutput output,
      AggMetaService aggMetaService) {
    ExecutorConfig config = new ExecutorConfig();
    config.setTopic(aggProperties.getTopic());
    config.setKafkaBootstrapServers(aggProperties.getKafkaBootstrapServers());
    config.setGroupId(aggProperties.getConsumerGroupId());

    String rack = System.getenv().getOrDefault("holoinsight.env.location", "");
    config.getConsumerProperties().put(ConsumerConfig.CLIENT_RACK_CONFIG, rack);

    config.getFaultToleranceConfig().setType(FaultToleranceConfig.Type.RECOMPUTE);

    StateConfig stateConfig = config.getFaultToleranceConfig().getStateConfig();
    stateConfig.setType(StateConfig.Type.JDBC);
    stateConfig.setDataSource(dataSource);

    config.getFaultToleranceConfig().setStateConfig(stateConfig);
    ExecutorManager m = new ExecutorManager(config);
    m.setAggTaskService(aggTaskService);
    m.setStateStore(stateStore);
    m.setCompletenessService(completenessService);
    m.setOutput(output);
    m.setAggMetaService(aggMetaService);
    return m;
  }

  @Bean
  public AggExecutorConfig aggExecutorConfig() {
    return new AggExecutorConfig();
  }

  @Bean
  public ExecutorInitRunner executorInitRunner() {
    return new ExecutorInitRunner();
  }

  @Bean
  public XOutput xConsoleOutput() {
    return new XConsoleOutput();
  }

  @Bean
  public AggMetaService aggMetaService(DataClientService dataClientService) {
    return new AggMetaServiceImpl(dataClientService);
  }
}
