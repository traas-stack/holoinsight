/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-09 17:31:00
 */
public class RequestContextAdapterImpl implements RequestContextAdapter {
  @Override
  public QueryProto.QueryRequest requestAdapte(QueryProto.QueryRequest request) {
    return request;
  }

  @Override
  public QueryProto.PqlRangeRequest requestAdapte(QueryProto.PqlRangeRequest request) {
    return request;
  }

  @Override
  public <T> void queryWrapperTenantAdapt(QueryWrapper<T> queryWrapper, String tenant,
      String workspace) {
    if (queryWrapper != null) {
      if (StringUtils.isNotBlank(tenant)) {
        queryWrapper.eq("tenant", tenant);
      }
      if (StringUtils.isNotBlank(workspace)) {
        queryWrapper.eq("workspace", workspace);
      }
    }
  }

  @Override
  public String getWorkspace(boolean cross) {
    MonitorScope ms = RequestContext.getContext().ms;
    return ms.getWorkspace();
  }
}
