/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.common.dao.entity.IntegrationProduct;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.IntegrationProductMapper;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCacheService.java, v 0.1 2022年03月21日 8:24 下午 jinsong.yjs Exp $
 */
@Slf4j
public class SuperCacheService extends ScheduleLoadTask {
  private SuperCache sc;

  @Autowired
  private MetaDictValueService metaDictValueService;

  @Autowired
  private MetricInfoService metricInfoService;
  @Resource
  private MetricInfoMapper metricInfoMapper;
  @Resource
  private IntegrationProductMapper integrationProductMapper;

  public SuperCache getSc() {
    return sc;
  }

  @Override
  public void load() throws Exception {
    ProdLog.info("[SuperCache] load start");
    SuperCache sc = new SuperCache();
    sc.metaDataDictValueMap = metaDictValueService.getMetaDictValue();
    ProdLog.info("[SuperCache][metaDataDictValueMap] size: " + sc.metaDataDictValueMap.size());
    sc.expressionMetricList = metricInfoService.querySpmList();
    ProdLog.info("[SuperCache][expressionMetricList] size: " + sc.expressionMetricList.size());

    sc.resourceKeys =
        sc.getListValue("global_config", "resource_keys", Collections.singletonList("tenant"));
    sc.freePrefixes =
        sc.getListValue("global_config", "free_metric_prefix", Collections.emptyList());
    sc.metricTypes = queryMetricTypes();
    sc.integrationProducts = queryIntegrationProducts();
    this.sc = sc;
    ProdLog.info("[SuperCache] load end");
  }

  private Set<String> queryIntegrationProducts() {
    QueryWrapper<IntegrationProduct> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("DISTINCT name");
    List<IntegrationProduct> integrationProducts =
        this.integrationProductMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(integrationProducts)) {
      return Collections.emptySet();
    }
    return integrationProducts.stream() //
        .map(IntegrationProduct::getName) //
        .collect(Collectors.toSet());
  }

  private Set<String> queryMetricTypes() {
    QueryWrapper<MetricInfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("DISTINCT metric_type");
    List<MetricInfo> metricInfoList = this.metricInfoMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(metricInfoList)) {
      return Collections.emptySet();
    }
    return metricInfoList.stream() //
        .map(MetricInfo::getMetricType) //
        .collect(Collectors.toSet());
  }

  @Override
  public int periodInSeconds() {
    return 60;
  }

  @Override
  public String getTaskName() {
    return "SuperCacheService";
  }

}
