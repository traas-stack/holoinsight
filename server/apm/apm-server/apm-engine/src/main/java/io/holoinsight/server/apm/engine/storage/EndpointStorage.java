/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.Endpoint;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EndpointStorage extends ReadableStorage {

  List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime,
      Map<String, String> termParams) throws IOException;
}
