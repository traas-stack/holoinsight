/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.core.service.bitmap.BitmapDataCoreService;
import io.holoinsight.server.meta.core.service.hashmap.HashMapDataCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DBCoreServiceSwitcher {

  @Autowired
  private HashMapDataCoreService hashMapDataCoreService;

  @Autowired
  private BitmapDataCoreService bitmapDataCoreService;

  @Autowired
  private SuperCacheService superCacheService;

  public DBCoreService dbCoreService() {
    boolean metaIndexBitMapEnable = true;
    try {
      metaIndexBitMapEnable = Boolean.parseBoolean(superCacheService.getSc().metaDataDictValueMap
          .getOrDefault("global_config", new HashMap<>()).get("meta_index_bitmap_enable")
          .getDictValue());
    } catch (Exception e) {
      metaIndexBitMapEnable = true;
    }
    DBCoreService service = metaIndexBitMapEnable ? bitmapDataCoreService : hashMapDataCoreService;
    service.startBuildIndex();
    return service;
  }

}
