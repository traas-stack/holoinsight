/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.apm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "holoinsight.query.apm")
@Component
@Slf4j
@Data
public class ApmClient {

  private String address;

  @Resource
  private TenantOpsMapper tenantOpsMapper;

  private LoadingCache<String, ApmAPI> clients = CacheBuilder.newBuilder()
      .expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<String, ApmAPI>() {
        @Override
        public ApmAPI load(String tenantName) throws Exception {
          return createApmAPI(tenantName);
        }
      });

  public ApmAPI getClient(String tenant) {
    try {
      return clients.get(tenant);
    } catch (Exception e) {
      log.error("apm client create fail: {}, msg={}", tenant, e.getMessage());
      return null;
    }
  }

  private ApmAPI createApmAPI(String tenant) {
    if (StringUtils.isBlank(tenant)) {
      tenant = "dev";
    }
    Assert.notNull(address, "apm config not found for tenant: " + tenant);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(60 * 3, TimeUnit.SECONDS)
        .connectTimeout(60 * 3, TimeUnit.SECONDS).build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(address)
        .addConverterFactory(JacksonConverterFactory.create(objectMapper)).client(okHttpClient)
        .build();

    return retrofit.create(ApmAPI.class);
  }
}
