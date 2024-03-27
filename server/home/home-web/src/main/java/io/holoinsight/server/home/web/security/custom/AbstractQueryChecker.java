/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.model.DataQueryRequest;
import io.holoinsight.server.home.facade.trigger.Filter;
import io.holoinsight.server.home.web.security.ApiSecurityService;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-02-28 18:39:00
 */
public abstract class AbstractQueryChecker implements LevelAuthorizationCheck {

  @Autowired
  protected ApiSecurityService apiSecurityService;

  protected LevelAuthorizationCheckResult checkMetricTableWithAlarmFilter(String metricTable,
      String tenant, String workspace, List<Filter> filters) {
    if (!checkSqlField(metricTable)) {
      return failCheckResult("invalid sql field for metric %s", metricTable);
    }
    if (apiSecurityService.isGlobalMetric(metricTable)) {
      boolean result = apiSecurityService.checkFilter(metricTable,
          getFilterMapFromAlarmFilter(filters), tenant, workspace);
      if (!result) {
        return failCheckResult("fail to check global metric %s with filters %s", metricTable,
            J.toJson(filters));
      }
    } else {
      boolean result =
          apiSecurityService.checkMetricTenantAndWorkspace(metricTable, tenant, workspace);
      if (!result) {
        return failCheckResult("fail to check metric %s with tenant %s workspace %s", metricTable,
            tenant, workspace);
      }
    }
    return successCheckResult();
  }

  protected boolean checkMetricTableWithQueryFilter(String metricTable, String tenant,
      String workspace, List<DataQueryRequest.QueryFilter> filters) {
    if (!checkSqlField(metricTable)) {
      return false;
    }
    if (apiSecurityService.isGlobalMetric(metricTable)) {
      return apiSecurityService.checkFilter(metricTable, getFilterMap(filters), tenant, workspace);
    } else {
      return apiSecurityService.checkMetricTenantAndWorkspace(metricTable, tenant, workspace);
    }
  }

  protected Map<String, List<Object>> getFilterMap(List<DataQueryRequest.QueryFilter> filters) {
    Map<String, List<Object>> filterMap = new HashMap<>();
    if (CollectionUtils.isEmpty(filters)) {
      return filterMap;
    }
    for (DataQueryRequest.QueryFilter filter : filters) {
      String type = filter.getType();
      List<Object> values = filterMap.computeIfAbsent(filter.getName(), k -> new ArrayList<>());
      switch (type) {
        case "literal_or":
          values.addAll(Arrays.asList(filter.getValue().split("\\|")));
          break;
        case "literal":
          values.add(filter.getValue());
          break;
      }
    }
    return filterMap;
  }

  private Map<String, List<Object>> getFilterMapFromAlarmFilter(List<Filter> filters) {
    Map<String, List<Object>> filterMap = new HashMap<>();
    if (CollectionUtils.isEmpty(filters)) {
      return filterMap;
    }
    for (Filter filter : filters) {
      String type = filter.getType();
      List<Object> values = filterMap.computeIfAbsent(filter.getName(), k -> new ArrayList<>());
      switch (type) {
        case "literal_or":
          values.addAll(Arrays.asList(filter.getValue().split("\\|")));
          break;
        case "literal":
          values.add(filter.getValue());
          break;
      }
    }
    return filterMap;
  }
}
