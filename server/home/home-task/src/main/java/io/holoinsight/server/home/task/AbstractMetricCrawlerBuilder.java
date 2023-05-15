/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static io.holoinsight.server.home.biz.common.MetaDictType.METRIC_CONFIG;

/**
 * @author jsy1001de
 * @version 1.0: AbstractMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 17:05
 */
@Slf4j
public abstract class AbstractMetricCrawlerBuilder implements MetricCrawlerBuilder {

  public List<MetricInfoModel> getMetricInfoModel(String product) {
    List<MetricInfoModel> value =
        MetaDictUtil.getValue(METRIC_CONFIG, product, new TypeToken<List<MetricInfoModel>>() {});
    if (null == value) {
      return new ArrayList<>();
    }
    return value;
  }

  @Data
  public static class MetricInfoModel {

    public String metric;
    public List<String> tags = new ArrayList<>();
    public List<MetricInfo> metricInfoList = new ArrayList<>();
  }

  public MetricInfo genMetricInfo(String tenant, String workspace, String organization,
      String product, String metric, String metricTable, String description, String unit,
      Integer period, List<String> tags) {

    MetricInfo metricInfo = new MetricInfo();
    metricInfo.setTenant(tenant);
    metricInfo.setWorkspace(workspace);
    metricInfo.setOrganization(organization);
    metricInfo.setProduct(product);
    metricInfo.setMetric(metric);
    metricInfo.setMetricTable(metricTable);
    metricInfo.setDescription(description);
    metricInfo.setUnit(unit);
    metricInfo.setPeriod(period);
    metricInfo.setTags(J.toJson(tags));
    metricInfo.setDeleted(false);
    return metricInfo;
  }
}
