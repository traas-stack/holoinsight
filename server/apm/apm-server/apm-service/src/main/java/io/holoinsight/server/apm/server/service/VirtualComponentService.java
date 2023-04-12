/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.VirtualComponent;

import java.util.List;
import java.util.Map;

public interface VirtualComponentService {
  List<VirtualComponent> getDbList(String tenant, String service, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  List<VirtualComponent> getCacheList(String tenant, String service, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  List<VirtualComponent> getMQList(String tenant, String service, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime, Map<String, String> termParams) throws Exception;

}
