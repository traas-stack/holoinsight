/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

  private Map<String, String> resourceKeys;

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
    if (productClosedDictVal != null) {
      productClosed = J.get().fromJson(productClosedDictVal.getDictValue(),
          new TypeToken<Map<String, Set<String>>>() {}.getType());
    }

    MetaDataDictValue resourceKeyDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("resource_keys");
    if (resourceKeyDictVal != null) {
      resourceKeys = J.get().fromJson(resourceKeyDictVal.getDictValue(),
          new TypeToken<Map<String, String>>() {}.getType());
    }


    log.info("[product_ctl] refresh closed products, switchOn={}, resourceKeys={}, closed={}",
        switchOn, resourceKeys, productClosed);
  }

  @Override
  public boolean switchOn() {
    return switchOn;
  }

  @Override
  public boolean productClosed(MonitorProductCode productCode, Map<String, String> tags) {
    String code = productCode.getCode();
    return switchOn && tags != null && resourceKeys != null && productClosed != null
        && productClosed.getOrDefault(tags.get(resourceKeys.get(code)), new HashSet<>())
            .contains(code);
  }


}
