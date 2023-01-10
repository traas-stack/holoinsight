/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service;

import io.holoinsight.server.storage.common.model.query.Call;
import io.holoinsight.server.storage.common.model.query.ResponseMetric;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TopologyEsService {
  List<Call> getTenantCalls(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws IOException;

  List<Call.DeepCall> getServiceInstanceCalls(String tenant, String service, String serviceInstance,
      long startTime, long endTime, String sourceOrDest, Map<String, String> termParams)
      throws IOException;

  List<Call.DeepCall> getEndpointCalls(String tenant, String service, String endpoint,
      long startTime, long endTime, String sourceOrDest, Map<String, String> termParams)
      throws IOException;

  List<Call> getComponentCalls(String tenant, String address, long startTime, long endTime,
      String sourceOrDest, Map<String, String> termParams) throws IOException;

  Map<String, ResponseMetric> getServiceAggMetric(String tenant, List<String> fieldValueList,
      long startTime, long endTime, String aggField, Map<String, String> termParams)
      throws IOException;
}
