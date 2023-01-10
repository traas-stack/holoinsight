/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.retrofitservice;

import io.holoinsight.server.home.common.service.ceresdb.CreateTenantRequest;
import io.holoinsight.server.home.common.service.ceresdb.CreateTenantResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *
 * @author jsy1001de
 * @version 1.0: CeresDBService.java, v 0.1 2022年06月21日 4:15 下午 jinsong.yjs Exp $
 */
public interface CeresDBRetrofitService {

    /**
     * 获取租户列表
     */
    @Headers("Content-Type: application/json")
    @POST("api/inner/admin/tenants")
    Call<CreateTenantResponse> createOrUpdateTenant(@Header("X-CeresDB-AccessUser") String accessUser,
                                                    @Header("X-CeresDB-AccessToken") String accessToken,
                                                    @Body CreateTenantRequest params);

}