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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/10/11 6:59 下午
 */
@Service
public class AIAlarmLoadData implements AlarmLoadData {

  private static Logger LOGGER = LoggerFactory.getLogger(AIAlarmLoadData.class);

  @Resource
  private QueryClientService queryClientService;

  @Override
  public List<DataResult> queryDataResult(ComputeTaskPackage computeTask,
      InspectConfig inspectConfig, Trigger trigger) {
    List<DataResult> dataResults = new ArrayList<>();
    QueryProto.QueryResponse response = queryDataCount(computeTask, inspectConfig, trigger);
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

  private QueryProto.QueryResponse queryDataCount(ComputeTaskPackage computeTask,
      InspectConfig inspectConfig, Trigger trigger) {
    QueryProto.QueryResponse response = null;
    try {
      long start = computeTask.getTimestamp() - 4L * PeriodType.HOUR.intervalMillis();
      long end = computeTask.getTimestamp() + PeriodType.MINUTE.intervalMillis();
      QueryProto.QueryResponse response1 = getData(inspectConfig, trigger, start, end);
      if (response1 == null || CollectionUtils.isEmpty(response1.getResultsList())) {
        return null;
      }
      long yesterdayPeriod = computeTask.getTimestamp() - PeriodType.DAY.intervalMillis();
      long yesterdayStart = yesterdayPeriod - 4L * PeriodType.HOUR.intervalMillis();
      long yesterdayEnd =
          yesterdayPeriod + PeriodType.HOUR.intervalMillis() + PeriodType.MINUTE.intervalMillis();
      QueryProto.QueryResponse response2 =
          getData(inspectConfig, trigger, yesterdayStart, yesterdayEnd);
      // If only have data for the day
      QueryProto.QueryResponse.Builder reponseBuilder = QueryProto.QueryResponse.newBuilder();
      if (response2 == null || CollectionUtils.isEmpty(response2.getResultsList())) {
        response1.getResultsList().forEach(result1 -> {
          List<QueryProto.Point> points = new ArrayList<>();
          points.addAll(result1.getPointsList());
          QueryProto.Result result = QueryProto.Result.newBuilder().addAllPoints(points)
              .setMetric(result1.getMetric()).putAllTags(result1.getTagsMap()).build();
          reponseBuilder.addResults(result);
        });
      } else {
        response1.getResultsList()
            .forEach(result1 -> response2.getResultsList().forEach(result2 -> {
              if (result2.getTagsMap().equals(result1.getTagsMap())) {
                List<QueryProto.Point> points = new ArrayList<>();
                points.addAll(result1.getPointsList());
                points.addAll(result2.getPointsList());
                QueryProto.Result result = QueryProto.Result.newBuilder().addAllPoints(points)
                    .setMetric(result1.getMetric()).putAllTags(result1.getTagsMap()).build();
                reponseBuilder.addResults(result);
              }
            }));
      }
      response = reponseBuilder.build();
    } catch (Exception e) {
      LOGGER.error("QueryData Exception", e);
    }
    return response;
  }

  private QueryProto.QueryResponse getData(InspectConfig inspectConfig, Trigger trigger, long start,
      long end) {
    QueryProto.QueryResponse response = null;
    QueryProto.QueryRequest request = null;
    try {
      List<QueryProto.Datasource> datasources = new ArrayList<>();
      for (DataSource dataSource : trigger.getDatasources()) {
        // 前端传 aggregator，后端判断其非 none 就把 downsample 设置为 1m，后侧置空
        String aggregator = dataSource.getAggregator();
        if (StringUtils.isNotEmpty(aggregator) && !aggregator.equals("none")) {
          dataSource.setDownsample("1m");
        }
        QueryProto.Datasource.Builder builder = QueryProto.Datasource.newBuilder()
            .setName(dataSource.getName()).setStart(start).setEnd(end)
            .setMetric(dataSource.getMetric()).addAllFilters(filterConvert(dataSource.getFilters()))
            .setAggregator(dataSource.getAggregator());

        if (dataSource.getGroupBy() != null) {
          builder.addAllGroupBy(dataSource.getGroupBy());
        }
        if (dataSource.getDownsample() != null) {
          builder.setDownsample(dataSource.getDownsample());
        }
        QueryProto.Datasource queryDatasource = builder.build();
        datasources.add(queryDatasource);
      }

      request = QueryProto.QueryRequest.newBuilder().setTenant(inspectConfig.getTenant())
          .setQuery(trigger.getQuery()).addAllDatasources(datasources).build();
      response = queryClientService.queryData(request, "AI_ALERT");
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("{} QueryData Success Request:{} Response:{}", inspectConfig.getTraceId(),
            G.get().toJson(request), G.get().toJson(response));
      }
    } catch (Exception e) {
      LOGGER.error("QueryData Exception Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response), e);
    }
    return response;
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
