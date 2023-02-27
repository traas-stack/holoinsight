/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.common.service.MetaDictValueService;
import io.holoinsight.server.home.common.util.ProdLog;
import io.holoinsight.server.home.common.util.ScheduleLoadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCacheService.java, v 0.1 2022年03月21日 8:24 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class SuperCacheService extends ScheduleLoadTask {
  private SuperCache sc;

  @Autowired
  private MetaDictValueService metaDictValueService;

  public SuperCache getSc() {
    return sc;
  }

  @Override
  public void load() throws Exception {
    ProdLog.info("[SuperCahce] load start");
    SuperCache sc = new SuperCache();
    sc.metaDataDictValueMap = metaDictValueService.getMetaDictValue();
    this.sc = sc;
    ProdLog.info("[SuperCahce] load end");
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
