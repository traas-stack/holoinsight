/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static io.holoinsight.server.home.biz.common.MetaDictType.METRIC_CONFIG;

/**
 * @author jsy1001de
 * @version 1.0: AbstractMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 17:05
 */
@Slf4j
public abstract class AbstractMetricCrawlerBuilder implements MetricCrawlerBuilder {

  @Override
  public List<MetricInfo> buildEntity(IntegrationProductDTO integrationProduct) {
    List<MetricInfo> metricInfoList = new ArrayList<>();
    log.info("[crawlerTask][{}], outer start", integrationProduct.getName());
    List<MetricInfoModel> metricInfoModels = getMetricInfoModel(integrationProduct.getName());
    if (CollectionUtils.isEmpty(metricInfoModels))
      return metricInfoList;

    for (MetricInfoModel model : metricInfoModels) {
      if (CollectionUtils.isEmpty(model.getTags()))
        continue;

      if (!CollectionUtils.isEmpty(model.getMetricInfoList())) {
        metricInfoList.addAll(model.getMetricInfoList());
      }
      List<MetricInfo> list =
          getMetricInfoList(model.getMetric(), model.getTags(), model.getMetricInfoTemplate());
      if (!CollectionUtils.isEmpty(list)) {
        metricInfoList.addAll(list);
      }
    }
    log.info("[crawlerTask][{}][{}], outer end", integrationProduct.getName(),
        metricInfoList.size());
    return metricInfoList;
  }

  protected abstract List<MetricInfo> getMetricInfoList(String metric, List<String> tags,
      MetricInfo metricInfoTemplate);


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
    public MetricInfo metricInfoTemplate;
  }

  public static MetricInfo genMetricInfo(String tenant, String workspace, String organization,
      String product, String metricType, String metric, String metricTable, String description,
      String unit, Integer period, List<String> tags) {

    MetricInfo metricInfo = new MetricInfo();
    metricInfo.setTenant(tenant);
    metricInfo.setWorkspace(workspace);
    metricInfo.setOrganization(organization);
    metricInfo.setProduct(product);
    metricInfo.setMetricType(metricType);
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
