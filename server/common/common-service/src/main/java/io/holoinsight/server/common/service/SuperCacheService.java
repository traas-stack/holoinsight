/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    sc.expressionMetricList = metricInfoService.querySpmList();
    this.sc = sc;
    ProdLog.info("[SuperCache] load end");
  }

  @Override
  public int periodInSeconds() {
    return 10;
  }

  @Override
  public String getTaskName() {
    return "SuperCacheService";
  }
}
