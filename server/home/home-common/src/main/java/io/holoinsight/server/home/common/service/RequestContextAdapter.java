/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.query.grpc.QueryProto;

import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-09 17:30:00
 */
public interface RequestContextAdapter {

  QueryProto.QueryRequest requestAdapte(QueryProto.QueryRequest request);

  QueryProto.PqlRangeRequest requestAdapte(QueryProto.PqlRangeRequest request);

  <T> void queryWrapperTenantAdapt(QueryWrapper<T> queryWrapper, String tenant, String workspace);

  String getWorkspace(boolean cross);
}
