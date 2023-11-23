/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.home.facade.utils;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-11-21 21:05:00
 */
public interface ApiSecurityService {

  boolean checkMetricTenantAndWorkspace(String metricTable, String tenant, String workspace);

  boolean checkFilter(String metricTable, Map<String, List<Object>> filters, String tenant,
      String workspace);

  boolean checkRuleTenantAndWorkspace(String uniqueId, String tenant, String workspace);

  boolean isGlobalMetric(String metricTable);

}
