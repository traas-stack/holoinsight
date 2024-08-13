/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch;

import io.holoinsight.server.apm.engine.elasticsearch.installer.EsModelInstaller;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.EndpointEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.EndpointRelationEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.EventEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.NetworkAddressMappingEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceErrorEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceInstanceEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceInstanceRelationEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceOverviewEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceRelationEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.SlowSqlEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.SpanEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.TopologyEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.VirtualComponentEsStorage;
import io.holoinsight.server.apm.engine.elasticsearch.ttl.EsDataCleaner;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiangwanpeng
 * @version : EsModelInstaller.java, v 0.1 2023年02月26日 21:26 xiangwanpeng Exp $
 */
@Data
@Slf4j
@Configuration
@ConditionalOnFeature("trace")
@ConditionalOnProperty(value = "holoinsight.storage.elasticsearch.enable", havingValue = "true")
@ConfigurationProperties(prefix = "holoinsight.storage.elasticsearch")
public class HoloinsightEsConfiguration {

  private List<String> hosts = new ArrayList<>();
  /** 默认端口9200 */
  private int port = 9200;

  private String user;
  private String password;
  private int shards = 5;
  private int replicas = 1;
  private int ttl = 7; // unit: day

  @Bean("elasticsearchClient")
  @Primary
  public RestHighLevelClient elasticsearchClient() {
    log.info("init es config, hosts={}, port={}", hosts, port);
    List<HttpHost> httpHosts =
        hosts.stream().map(host -> new HttpHost(host, port)).collect(Collectors.toList());
    RestClientBuilder builder =
        RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]));
    builder.setStrictDeprecationMode(false);
    if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(password)) {
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(user, password));
      builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
          .setDefaultCredentialsProvider(credentialsProvider));
    }
    return new RestHighLevelClient(builder);
  }

  @Bean("esModelInstaller")
  @Primary
  public EsModelInstaller esModelInstaller() {
    return new EsModelInstaller();
  }

  @Bean("esDataCleaner")
  @Primary
  public EsDataCleaner esDataCleaner() {
    return new EsDataCleaner();
  }

  @Bean("spanEsStorage")
  @Primary
  public SpanEsStorage spanEsStorage() {
    return new SpanEsStorage();
  }

  @Bean("endpointRelationEsStorage")
  @Primary
  public EndpointRelationEsStorage endpointRelationEsStorage() {
    return new EndpointRelationEsStorage();
  }

  @Bean("endpointEsStorage")
  @Primary
  public EndpointEsStorage endpointEsStorage() {
    return new EndpointEsStorage();
  }

  @Bean("networkAddressMappingEsStorage")
  @Primary
  public NetworkAddressMappingEsStorage networkAddressMappingEsStorage() {
    return new NetworkAddressMappingEsStorage();
  }

  @Bean("serviceErrorEsStorage")
  @Primary
  public ServiceErrorEsStorage serviceErrorEsStorage() {
    return new ServiceErrorEsStorage();
  }

  @Bean("serviceInstanceRelationEsStorage")
  @Primary
  public ServiceInstanceRelationEsStorage serviceInstanceRelationEsStorage() {
    return new ServiceInstanceRelationEsStorage();
  }

  @Bean("serviceInstanceEsStorage")
  @Primary
  public ServiceInstanceEsStorage serviceInstanceEsStorage() {
    return new ServiceInstanceEsStorage();
  }

  @Bean("serviceOverviewEsStorage")
  @Primary
  public ServiceOverviewEsStorage serviceOverviewEsStorage() {
    return new ServiceOverviewEsStorage();
  }

  @Bean("serviceRelationEsStorage")
  @Primary
  public ServiceRelationEsStorage serviceRelationEsStorage() {
    return new ServiceRelationEsStorage();
  }

  @Bean("slowSqlEsStorage")
  @Primary
  public SlowSqlEsStorage slowSqlEsStorage() {
    return new SlowSqlEsStorage();
  }

  @Bean("topologyEsStorage")
  @Primary
  public TopologyEsStorage topologyEsStorage() {
    return new TopologyEsStorage();
  }

  @Bean("virtualComponentEsStorage")
  @Primary
  public VirtualComponentEsStorage virtualComponentEsStorage() {
    return new VirtualComponentEsStorage();
  }

  @Bean("eventEsStorage")
  @Primary
  public EventEsStorage eventEsStorage() {
    return new EventEsStorage();
  }
}
