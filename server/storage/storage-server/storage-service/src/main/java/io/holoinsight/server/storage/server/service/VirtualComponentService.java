/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.common.model.query.VirtualComponent;

import java.io.IOException;
import java.util.List;

public interface VirtualComponentService {
  List<VirtualComponent> getDbList(String tenant, String service, long startTime, long endTime)
      throws IOException;

  List<VirtualComponent> getCacheList(String tenant, String service, long startTime, long endTime)
      throws IOException;

  List<VirtualComponent> getMQList(String tenant, String service, long startTime, long endTime)
      throws IOException;

  List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime) throws IOException;

}
