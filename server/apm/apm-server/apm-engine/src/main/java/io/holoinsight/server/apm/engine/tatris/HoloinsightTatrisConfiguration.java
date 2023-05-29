/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris;

import io.holoinsight.server.apm.engine.tatris.installer.TatrisModelInstaller;
import io.holoinsight.server.apm.engine.tatris.storage.impl.*;
import io.holoinsight.server.apm.engine.tatris.ttl.TatrisDataCleaner;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangwanpeng
 * @version : TatrisModelInstaller.java, v 0.1 2023年02月26日 21:26 xiangwanpeng Exp $
 */
@Data
@Slf4j
@Configuration
@ConditionalOnFeature("trace")
@ConfigurationProperties(prefix = "holoinsight.storage.tatris")
@ConditionalOnProperty(value = "holoinsight.storage.tatris.enable", havingValue = "true")
public class HoloinsightTatrisConfiguration {

    private String host;
    private int port = 6060;

    @Bean("tatrisClient")
    public RestHighLevelClient elasticsearchClient() {
        log.info("init tatris config, host={}, port={}", host, port);
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        builder.setStrictDeprecationMode(false);
        return new RestHighLevelClient(builder);
    }

    @Bean("tatrisModelInstaller")
    public TatrisModelInstaller tatrisModelInstaller() {
        return new TatrisModelInstaller();
    }

    @Bean("tatrisDataCleaner")
    public TatrisDataCleaner tatrisDataCleaner() {
        return new TatrisDataCleaner();
    }

    @Bean("spanTatrisStorage")
    public SpanTatrisStorage spanTatrisStorage() {
        return new SpanTatrisStorage();
    }

    @Bean("spanMetricTatrisStorage")
    public SpanMetricTatrisStorage spanMetricTatrisStorage() {
        return new SpanMetricTatrisStorage();
    }

    @Bean("endpointRelationTatrisStorage")
    public EndpointRelationTatrisStorage endpointRelationTatrisStorage() {
        return new EndpointRelationTatrisStorage();
    }

    @Bean("endpointTatrisStorage")
    public EndpointTatrisStorage endpointTatrisStorage() {
        return new EndpointTatrisStorage();
    }

    @Bean("networkAddressMappingTatrisStorage")
    public NetworkAddressMappingTatrisStorage networkAddressMappingTatrisStorage() {
        return new NetworkAddressMappingTatrisStorage();
    }

    @Bean("serviceErrorTatrisStorage")
    public ServiceErrorTatrisStorage serviceErrorTatrisStorage() {
        return new ServiceErrorTatrisStorage();
    }

    @Bean("serviceInstanceRelationTatrisStorage")
    public ServiceInstanceRelationTatrisStorage serviceInstanceRelationTatrisStorage() {
        return new ServiceInstanceRelationTatrisStorage();
    }

    @Bean("serviceInstanceTatrisStorage")
    public ServiceInstanceTatrisStorage serviceInstanceTatrisStorage() {
        return new ServiceInstanceTatrisStorage();
    }

    @Bean("serviceOverviewTatrisStorage")
    public ServiceOverviewTatrisStorage serviceOverviewTatrisStorage() {
        return new ServiceOverviewTatrisStorage();
    }

    @Bean("serviceRelationTatrisStorage")
    public ServiceRelationTatrisStorage serviceRelationTatrisStorage() {
        return new ServiceRelationTatrisStorage();
    }

    @Bean("slowSqlTatrisStorage")
    public SlowSqlTatrisStorage slowSqlTatrisStorage() {
        return new SlowSqlTatrisStorage();
    }

    @Bean("topologyTatrisStorage")
    public TopologyTatrisStorage topologyTatrisStorage() {
        return new TopologyTatrisStorage();
    }

    @Bean("virtualComponentTatrisStorage")
    public VirtualComponentTatrisStorage virtualComponentTatrisStorage() {
        return new VirtualComponentTatrisStorage();
    }
}
