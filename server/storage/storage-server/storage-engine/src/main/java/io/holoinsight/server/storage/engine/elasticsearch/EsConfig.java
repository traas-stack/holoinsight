/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch;

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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO see <a href="https://blog.csdn.net/cloudbigdata/article/details/126296206">干货 |
 * Elasticsearch Java 客户端演进历史和选型指南</a>
 * 
 * @author jiwliu
 * @version : RestClientConfig.java, v 0.1 2022年09月18日 15:04 wanpeng.xwp Exp $
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "holoinsight.storage.elasticsearch")
@ConditionalOnFeature("trace")
public class EsConfig {

  private List<String> hosts = new ArrayList<>();
  /**
   * 默认端口9200
   */
  private int port = 9200;
  private String user;
  private String password;

  @Bean
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

}
