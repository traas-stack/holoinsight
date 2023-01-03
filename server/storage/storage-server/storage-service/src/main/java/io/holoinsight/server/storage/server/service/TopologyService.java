/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.common.model.query.Topology;

import java.io.IOException;
import java.util.Map;

public interface TopologyService {
    Topology getTenantTopology(String tenant, long startTime, long endTime, Map<String, String> termParams) throws IOException;

    Topology getServiceTopology(String tenant, String service, long startTime, long endTime, int depth, Map<String, String> termParams) throws IOException;
    Topology getServiceInstanceTopology(String tenant, String service, String serviceInstance, long startTime, long endTime, int depth, Map<String, String> termParams) throws IOException;
    Topology getEndpointTopology(String tenant, String service, String endpoint, long startTime, long endTime, int depth, Map<String, String> termParams) throws IOException;
    Topology getDbTopology(String tenant, String address, long startTime, long endTime, Map<String, String> termParams) throws IOException;
    Topology getMQTopology(String tenant, String address, long startTime, long endTime, Map<String, String> termParams) throws IOException;

}