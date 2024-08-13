/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.Topology;

import java.util.Map;

public interface TopologyService {
  Topology getTenantTopology(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  Topology getServiceTopology(String tenant, String service, long startTime, long endTime,
      int depth, Map<String, String> termParams) throws Exception;

  Topology getServiceInstanceTopology(String tenant, String service, String serviceInstance,
      long startTime, long endTime, int depth, Map<String, String> termParams) throws Exception;

  Topology getEndpointTopology(String tenant, String service, String endpoint, long startTime,
      long endTime, int depth, Map<String, String> termParams) throws Exception;

  Topology getDbTopology(String tenant, String address, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  Topology getMQTopology(String tenant, String address, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

}
