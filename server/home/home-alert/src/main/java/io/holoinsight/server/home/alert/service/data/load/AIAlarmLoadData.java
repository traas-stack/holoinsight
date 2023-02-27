/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data.load;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.data.DataSource;
import io.holoinsight.server.home.alert.model.data.Filter;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.emuns.PeriodType;
import io.holoinsight.server.home.alert.model.trigger.Trigger;
import io.holoinsight.server.home.alert.service.data.AlarmLoadData;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
        dataResults.add(dataResult);
      }
    }
    return dataResults;
  }

  private QueryProto.QueryResponse queryTags(ComputeTaskPackage computeTask, InspectConfig e,
      Trigger trigger) {
    QueryProto.QueryResponse response = null;
    QueryProto.QueryRequest request = null;
    try {
      List<QueryProto.Datasource> datasources = new ArrayList<>();
      for (DataSource dataSource : trigger.getDatasources()) {
        // 获取检测那一分钟的tags
        long start = computeTask.getTimestamp() - PeriodType.MINUTE.intervalMillis();
        long end = computeTask.getTimestamp();

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

      request = QueryProto.QueryRequest.newBuilder().setTenant(e.getTenant())
          .setQuery(trigger.getQuery()).addAllDatasources(datasources).build();


      response = queryClientService.queryTag(request);
      LOGGER.info("QueryTags Success Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response));
      return response;
    } catch (Exception exception) {
      LOGGER.error("QueryTags Exception Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response), exception);
    }
    return null;
  }

  private QueryProto.QueryResponse queryDataCount(ComputeTaskPackage computeTask, InspectConfig e,
      Trigger trigger) {
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
        // 获取检测那一分钟的数据
        long start = computeTask.getTimestamp() - PeriodType.MINUTE.intervalMillis();
        long end = computeTask.getTimestamp();

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

      request = QueryProto.QueryRequest.newBuilder().setTenant(e.getTenant())
          .setQuery(trigger.getQuery()).addAllDatasources(datasources).build();


      response = queryClientService.queryData(request);
      LOGGER.debug("QueryData Success Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response));
      return response;
    } catch (Exception exception) {
      LOGGER.error("QueryData Exception Request:{} Response:{}", G.get().toJson(request),
          G.get().toJson(response), exception);
    }
    return null;
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
