/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

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
    this.sc = sc;
    ProdLog.info("[SuperCache] load end");
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
