/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorageHook;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.model.Record;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2024/2/5
 *
 * @author xzchaoo
 */
@Slf4j
public class AggSpanStorageHook implements SpanStorageHook {
  @Autowired
  protected AggDispatcher aggDispatcher;

  @Override
  public void beforeStorage(List<SpanDO> spans) {
    if (CollectionUtils.isEmpty(spans)) {
      return;
    }
    Map<String, List<SpanDO>> byTenant = new HashMap<>();
    for (SpanDO span : spans) {
      String tenant = (String) span.getTags().get(SpanDO.resource(SpanDO.TENANT));
      if (StringUtils.isEmpty(tenant)) {
        continue;
      }
      byTenant.computeIfAbsent(tenant, i -> new ArrayList<>()).add(span);
    }

    byTenant.forEach((tenant, list) -> {
      AuthInfo ai = new AuthInfo();
      ai.setTenant(tenant);
      aggDispatcher.dispatchSpans(ai, list);
    });
  }

  @Override
  public void beforeStorageServiceRelation(List<ServiceRelationDO> list) {
    String name = "_span_service_relation";
    Map<String, List<Record>> byTenant = new HashMap<>();

    for (ServiceRelationDO relation : list) {
      Record record = new Record();
      record.setTimestamp(relation.getEndTime());
      record.setName(name);

      record.setTags(buildStorageServiceRelationTags(relation));

      Map<String, Object> fields = Maps.newHashMapWithExpectedSize(1);
      fields.put("latency", relation.getLatency());
      record.setFields(fields);

      byTenant.computeIfAbsent(relation.getTenant(), i -> new ArrayList<>()).add(record);
    }

    dispatch(name, byTenant);
  }

  @Override
  public void beforeStorageServiceError(List<ServiceErrorDO> list) {
    String name = "_span_service_error";
    Map<String, List<Record>> byTenant = new HashMap<>();

    for (ServiceErrorDO e : list) {
      Record record = new Record();
      record.setTimestamp(e.getTimestamp());
      record.setName(name);

      record.setTags(buildStorageServiceErrorTags(e));

      Map<String, Object> fields = Maps.newHashMapWithExpectedSize(1);
      fields.put("latency", e.getLatency());
      record.setFields(fields);

      byTenant.computeIfAbsent(e.getTenant(), i -> new ArrayList<>()).add(record);
    }

    dispatch(name, byTenant);
  }

  protected Map<String, String> buildStorageServiceErrorTags(ServiceErrorDO e) {
    Map<String, String> tags = new HashMap<>();
    tags.put("trace_id", e.getTraceId());
    tags.put("resource.service.name", e.getServiceName());
    tags.put("resource.service.instance.name", e.getServiceInstanceName());
    tags.put("endpoint_name", e.getEndpointName());
    tags.put("error_kind", e.getErrorKind());
    tags.put("resource.tenant", e.getTenant());
    return tags;
  }

  protected Map<String, String> buildStorageServiceRelationTags(ServiceRelationDO e) {
    Map<String, String> tags = new HashMap<>();
    tags.put("trace_id", e.getTraceId());
    tags.put("dest_service_name", e.getDestServiceName());
    tags.put("component", e.getComponent());
    tags.put("type", e.getType());
    tags.put("resource.tenant", e.getTenant());
    return tags;
  }

  protected void dispatch(String name, Map<String, List<Record>> byTenant) {
    byTenant.forEach((tenant, list) -> {
      AuthInfo ai = new AuthInfo();
      ai.setTenant(tenant);
      aggDispatcher.dispatchRecords(ai, name, list);
    });
  }
}
