/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Filter;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/10/11 9:44 下午
 */
@Data
public class ExtendConfig {

  private Map<String, Object> dataInfo;

  private Map<String, Map<Long, Double>> trendData;

  public static ExtendConfig triggerConverter(DataResult dataResult,
      FunctionConfigAIParam functionConfigAIParam) {
    ExtendConfig extendConfig = new ExtendConfig();
    if (functionConfigAIParam != null && functionConfigAIParam.getTrigger() != null) {
      // 组装算法查询数据入参
      Map<String, Object> dataInfo = new HashMap<>();
      Map<String, Object> datasource = new HashMap<>();
      Map<String, Map<Long, Double>> trendData = new HashMap<>();
      List<DataSource> triggetDataSources = functionConfigAIParam.getTrigger().getDatasources();
      // 目前算法只有单数据源
      DataSource triggetDataSource = triggetDataSources.get(0);
      datasource.put("metric", triggetDataSource.getMetric());
      datasource.put("aggregator", triggetDataSource.getAggregator());
      datasource.put("downsample", triggetDataSource.getDownsample());
      datasource.put("groupBy", triggetDataSource.getGroupBy());
      datasource.put("start", functionConfigAIParam.getPeriod());
      datasource.put("end", functionConfigAIParam.getPeriod());
      // 指定tag
      List<Filter> filters = new ArrayList<>();
      for (String key : dataResult.getTags().keySet()) {
        Filter filter = new Filter();
        // 全值匹配
        filter.setType("literal");
        filter.setName(key);
        filter.setValue(dataResult.getTags().get(key));
        filters.add(filter);
      }
      datasource.put("filters", filters);
      List<Map<String, Object>> datasources = new ArrayList<>();
      datasources.add(datasource);

      dataInfo.put("tenant", functionConfigAIParam.getTenant());
      dataInfo.put("datasources", datasources);
      extendConfig.setDataInfo(dataInfo);

      trendData.put(triggetDataSource.getMetric(), dataResult.getPoints());
      extendConfig.setTrendData(trendData);
    }
    return extendConfig;
  }

}
