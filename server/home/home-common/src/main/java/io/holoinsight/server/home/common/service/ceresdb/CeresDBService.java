/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.ceresdb;

import io.holoinsight.server.home.common.service.retrofitapi.CeresDBRetrofitApiService;
import io.holoinsight.server.home.common.service.retrofitservice.CeresDBRetrofitService;
import io.holoinsight.server.home.common.util.retrofit.RetrofitApiFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author jsy1001de
 * @version 1.0: CeresDBRequest.java, v 0.1 2022年06月21日 4:34 下午 jinsong.yjs Exp $
 */
@Service
@Slf4j
public class CeresDBService {

  @Value("${ceresdb.host}")
  public String ceresdbhost;

  @Value("${ceresdb.port}")
  public String ceresdbport;

  @Value("${ceresdb.accessUser}")
  public String ceresdbuser;

  @Value("${ceresdb.accessToken}")
  public String ceresdbtoken;

  private static CeresDBRetrofitApiService client = null;

  public void init() {
    client = new RetrofitApiFactory().create("http://" + ceresdbhost + ":" + ceresdbport,
        CeresDBRetrofitApiService.class, CeresDBRetrofitService.class);

  }


  public CreateTenantResponse createOrUpdateTenant(String tenant, String token, Integer ttl) {
    CreateTenantRequest params = new CreateTenantRequest();
    params.setName(tenant);
    params.setToken(token);
    params.setTtlHour(ttl);

    return client.createOrUpdateTenant(ceresdbuser, ceresdbtoken, params);
  }
}
