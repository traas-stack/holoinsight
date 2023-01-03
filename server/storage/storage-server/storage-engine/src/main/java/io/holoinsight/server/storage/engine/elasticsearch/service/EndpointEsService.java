/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service;

import io.holoinsight.server.storage.common.model.query.Endpoint;

import java.io.IOException;
import java.util.List;

public interface EndpointEsService {

    List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime) throws IOException;
//    List<BizopsEndpoint> getEntryEndpointList(String tenant, long startTime, long endTime) throws IOException;
//    List<BizopsEndpoint> getErrorCodeList(String tenant, String service, String endpoint, boolean isEntry, int traceIdSize, long startTime, long endTime) throws IOException;
}
