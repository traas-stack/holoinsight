/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.test;

import io.holoinsight.server.common.JsonResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 *
 * @author jsy1001de
 * @version 1.0: TestService.java, v 0.1 2022年02月23日 2:10 下午 jinsong.yjs Exp $
 */
public interface TestService {

  // 定义一条接口请求
  @POST("/internal/api/meta/table")
  Call<JsonResult<TableInfo>> createTable(@Body TableInfo table);

  @GET("/internal/api/meta/tsd/query/{name}")
  Call<JsonResult<TableInfo>> queryTable(@Path("name") String name);
}
