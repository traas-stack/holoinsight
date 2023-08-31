/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ProductCtlServiceImpl implements ProductCtlService {

  @Autowired
  private SuperCacheService superCacheService;


  private Map<String, Set<String>> productClosed;

  private boolean switchOn = false;


  @Scheduled(initialDelay = 10000L, fixedDelay = 60000L)
  private void refresh() {

    MetaDataDictValue switchOnDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed_switch_on");
    try {
      switchOn = Boolean.parseBoolean(switchOnDictVal.getDictValue());
    } catch (Exception e) {
      switchOn = false;
    }

    MetaDataDictValue productClosedDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed");
    productClosed = J.get().fromJson(productClosedDictVal.getDictValue(),
        new TypeToken<Map<String, Set<String>>>() {}.getType());



    log.info("[product_ctl] refresh closed products, closed={}", productClosed);
  }

  @Override
  public boolean switchOn() {
    return switchOn;
  }

  @Override
  public boolean productClosed(MonitorProductCode productCode, String uniqueId) {
    return switchOn && productClosed != null
        && productClosed.getOrDefault(uniqueId, new HashSet<>()).contains(productCode.getCode());
  }

}
