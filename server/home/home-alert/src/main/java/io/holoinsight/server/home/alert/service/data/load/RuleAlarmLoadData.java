/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data.load;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.data.AlarmLoadData;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Filter;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
  public List<DataResult> queryDataResult(ComputeTaskPackage computeTask,
      InspectConfig inspectConfig, Trigger trigger) {
    List<DataResult> dataResults = new ArrayList<>();
    QueryProto.QueryResponse response = queryData(computeTask, inspectConfig, trigger);
    if (response != null) {
      for (QueryProto.Result result : response.getResultsList()) {
        DataResult dataResult = new DataResult();
        dataResult.setMetric(result.getMetric());
        dataResult.setTags(result.getTagsMap());
        Map<Long, Double> points = new HashMap<>();
        for (QueryProto.Point point : result.getPointsList()) {
          points.put(point.getTimestamp(), point.getValue());
        }
        dataResult.setPoints(points);
        dataResults.add(dataResult);
      }
    }
    return dataResults;
  }

  private QueryProto.QueryResponse queryData(ComputeTaskPackage computeTask, InspectConfig e,
      Trigger trigger) {
    QueryProto.QueryResponse response = null;
    QueryProto.QueryRequest request = null;
    try {
      request = buildRequest(computeTask.getTimestamp(), e.getTenant(), trigger);
      response = queryClientService.queryData(request);
      return response;
    } catch (Exception exception) {
      LOGGER.error("QueryData Exception Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response), exception);
    }
    return null;
  }

  protected QueryProto.QueryRequest buildRequest(long timestamp, String tenant, Trigger trigger) {
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
      String aggregator = dataSource.getAggregator();
      if (StringUtils.isNotEmpty(aggregator) && StringUtils.isBlank(downsample)
          && !aggregator.equals("none")) {
        downsample = "1m";
      }
      long start =
          timestamp - (trigger.getStepNum() - 1) * time * PeriodType.MINUTE.intervalMillis()
              - trigger.getDownsample() * PeriodType.MINUTE.intervalMillis();
      long end = timestamp + time * PeriodType.MINUTE.intervalMillis();
      QueryProto.SlidingWindow slidingWindow =
          QueryProto.SlidingWindow.newBuilder().setAggregator(trigger.getAggregator())
              .setWindowMs(trigger.getDownsample() * 60_000L).build();

      QueryProto.Datasource.Builder builder = QueryProto.Datasource.newBuilder()
          .setName(dataSource.getName()).setStart(start).setEnd(end)
          .setMetric(dataSource.getMetric()).addAllFilters(filterConvert(dataSource.getFilters()))
          .setAggregator(dataSource.getAggregator()).setSlidingWindow(slidingWindow);

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

  private List<QueryProto.QueryFilter> filterConvert(List<Filter> filters) {
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
