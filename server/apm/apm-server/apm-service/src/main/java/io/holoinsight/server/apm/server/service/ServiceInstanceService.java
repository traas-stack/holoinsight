/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.ServiceInstance;

import java.util.List;
import java.util.Map;

public interface ServiceInstanceService {

  List<ServiceInstance> getServiceInstanceList(String tenant, String service, long startTime,
      long endTime, Map<String, String> termParams) throws Exception;
}
