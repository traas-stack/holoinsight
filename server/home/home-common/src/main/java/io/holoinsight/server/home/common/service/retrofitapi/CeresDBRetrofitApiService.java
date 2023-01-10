/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.retrofitapi;


import io.holoinsight.server.home.common.service.ceresdb.CreateTenantRequest;
import io.holoinsight.server.home.common.service.ceresdb.CreateTenantResponse;

/**
 *
 * @author jsy1001de
 * @version 1.0: CeresDBApiService.java, v 0.1 2022年06月21日 4:33 下午 jinsong.yjs Exp $
 */
public interface CeresDBRetrofitApiService {

    CreateTenantResponse createOrUpdateTenant(String accessUser, String accessToken, CreateTenantRequest params);
}