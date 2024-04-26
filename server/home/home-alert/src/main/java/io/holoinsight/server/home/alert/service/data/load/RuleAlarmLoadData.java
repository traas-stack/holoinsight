/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data.load;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.emuns.PeriodType;
import io.holoinsight.server.common.dao.entity.dto.InspectConfig;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.DataSource;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.Filter;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.Trigger;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.data.AlarmLoadData;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/10/11 18:59
 */
@Service
public class RuleAlarmLoadData implements AlarmLoadData {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleAlarmLoadData.class);

  @Resource
  private QueryClientService queryClientService;

  @Override
  public List<TriggerDataResult> queryDataResult(ComputeTaskPackage computeTask,
      InspectConfig inspectConfig, Trigger trigger) {
    List<TriggerDataResult> triggerDataResults = new ArrayList<>();
    QueryProto.QueryResponse response = null;
    QueryProto.QueryRequest request = null;
    QueryProto.QueryRequest deltaRequest = null;
    QueryProto.QueryResponse deltaResponse = null;
    try {
      request = buildRequest(computeTask.getTimestamp(), inspectConfig.getTenant(), trigger);
      LOGGER.debug("{} alert query request {}", inspectConfig.getTraceId(), request.toString());
      response = queryClientService.queryData(request, "ALERT");
      triggerDataResults = merge(triggerDataResults, response);
      long deltaTimestamp = getDeltaTimestamp(trigger.getPeriodType());
      if (deltaTimestamp > 0) {
        deltaRequest = buildRequest(computeTask.getTimestamp() - deltaTimestamp,
            inspectConfig.getTenant(), trigger);
        deltaResponse = queryClientService.queryData(deltaRequest, "ALERT");
        triggerDataResults = merge(triggerDataResults, deltaResponse);
      }
    } catch (Exception exception) {
      LOGGER.error(
          "{} QueryData Exception Request:{} Response:{} DeltaRequest:{} DeltaResponse: {}",
          inspectConfig.getTraceId(), J.toJson(request), J.toJson(response), J.toJson(deltaRequest),
          J.toJson(deltaResponse), exception);
    }
    return triggerDataResults;
  }

  private long getDeltaTimestamp(PeriodType periodType) {
    if (periodType == null) {
      return 0;
    } else {
      return periodType.intervalMillis();
    }
  }

  protected List<TriggerDataResult> merge(List<TriggerDataResult> triggerDataResults,
      QueryProto.QueryResponse response) {
    Map<String /* data result key */, TriggerDataResult> map = new HashMap<>();
    if (!CollectionUtils.isEmpty(triggerDataResults)) {
      for (TriggerDataResult triggerDataResult : triggerDataResults) {
        map.put(triggerDataResult.getKey(), triggerDataResult);
      }
    }
    if (response != null && !CollectionUtils.isEmpty(response.getResultsList())) {
      for (QueryProto.Result result : response.getResultsList()) {
        TriggerDataResult key = new TriggerDataResult();
        key.setMetric(result.getMetric());
        key.setTags(result.getTagsMap());
        TriggerDataResult triggerDataResult = map.computeIfAbsent(key.getKey(), k -> key);
        if (triggerDataResult.getPoints() == null) {
          triggerDataResult.setPoints(new HashMap<>());
        }
        for (QueryProto.Point point : result.getPointsList()) {
          triggerDataResult.getPoints().put(point.getTimestamp(), point.getValue());
        }
      }
    }
    return new ArrayList<>(map.values());
  }

  public QueryProto.QueryRequest buildRequest(long timestamp, String tenant, Trigger trigger) {
    QueryProto.QueryRequest request = null;
    List<QueryProto.Datasource> datasources = new ArrayList<>();
    for (DataSource dataSource : trigger.getDatasources()) {
      long time = 1L;
      // Compatible format: 1m or 1m-avg
      String downsample = dataSource.getDownsample();
      if (StringUtils.isNotBlank(downsample)) {
        time = parseTime(downsample);
      }
      // If aggregator is not none, which means it is a valid aggregation, then downsample cannot be
      // null, and the default value 1m can be set.
      QueryProto.SlidingWindow slidingWindow = null;
      String aggregator = dataSource.getAggregator();
      if (StringUtils.isNotEmpty(aggregator) && StringUtils.isBlank(downsample)) {
        if (!aggregator.equals("none")) {
          downsample = "1m";
        }
      }

      if (!aggregator.equals("none")) {
        slidingWindow = QueryProto.SlidingWindow.newBuilder().setAggregator(trigger.getAggregator())
            .setWindowMs(trigger.getDownsample() * 60_000L).build();
      }

      long start =
          timestamp - (trigger.getStepNum() - 1) * time * PeriodType.MINUTE.intervalMillis()
              - trigger.getDownsample() * PeriodType.MINUTE.intervalMillis();
      long end = timestamp + time * PeriodType.MINUTE.intervalMillis();

      QueryProto.Datasource.Builder builder = QueryProto.Datasource.newBuilder()
          .setName(dataSource.getName()).setStart(start).setEnd(end)
          .setMetric(dataSource.getMetric()).addAllFilters(filterConvert(dataSource.getFilters()))
          .setAggregator(dataSource.getAggregator());

      if (slidingWindow != null) {
        builder.setSlidingWindow(slidingWindow);
      }

      if (dataSource.getGroupBy() != null) {
        builder.addAllGroupBy(dataSource.getGroupBy());
      }
      if (StringUtils.isNotEmpty(downsample)) {
        builder.setDownsample(downsample);
      }
      QueryProto.Datasource queryDatasource = builder.build();
      datasources.add(queryDatasource);

    }

    request = QueryProto.QueryRequest.newBuilder().setTenant(tenant).setQuery(trigger.getQuery())
        .addAllDatasources(datasources).build();
    return request;
  }

  private long parseTime(String downsample) {
    String timeStr = downsample;
    if (timeStr.contains("-")) {
      timeStr = timeStr.split("-", 2)[0];
    }
    return Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
  }

  public static List<QueryProto.QueryFilter> filterConvert(List<Filter> filters) {
    if (filters == null) {
      return new ArrayList<>();
    }
    List<QueryProto.QueryFilter> filterList = new ArrayList<>();
    filters.forEach(filter -> {
      QueryProto.QueryFilter queryFilter = QueryProto.QueryFilter.newBuilder()
          .setType(filter.getType()).setName(filter.getName()).setValue(filter.getValue()).build();
      filterList.add(queryFilter);
    });
    return filterList;
  }
}
