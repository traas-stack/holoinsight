/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface ServiceErrorStorage extends WritableStorage<ServiceErrorDO>, ReadableStorage {

  List<Map<String, String>> getServiceErrorList(String tenant, String serviceName, long startTime,
      long endTime, Map<String, String> termParams) throws IOException;

  List<Map<String, String>> getServiceErrorDetail(String tenant, String serviceName, long startTime,
      long endTime, Map<String, String> termParams) throws IOException;
}
