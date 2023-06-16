/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;

import java.util.List;
import java.util.Map;


public interface ServiceErrorService {

  void insert(List<ServiceErrorDO> serviceErrorEsDOList) throws Exception;

  List<Map<String, String>> getServiceErrorList(String tenant, String serviceName, long startTime,
      long endTime, Map<String, String> termParams) throws Exception;

  List<Map<String, String>> getServiceErrorDetail(String tenant, String serviceName, long startTime,
      long endTime, Map<String, String> termParams) throws Exception;
}
