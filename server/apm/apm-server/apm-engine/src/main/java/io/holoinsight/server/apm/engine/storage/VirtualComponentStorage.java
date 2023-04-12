/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.RequestType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VirtualComponentStorage extends ReadableStorage {

  List<VirtualComponent> getComponentList(String tenant, String service, long startTime,
      long endTime, RequestType type, String sourceOrDest, Map<String, String> termParams)
      throws IOException;

  List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime, Map<String, String> termParams) throws IOException;
}
