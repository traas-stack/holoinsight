/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.common.service;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@Data
@Slf4j
public class ResourceKeysHolder {

  private List<String> resourceKeys = Arrays.asList("tenant");

  @Autowired
  private SuperCacheService superCacheService;

  @Scheduled(initialDelay = 10000L, fixedRate = 10000L)
  private void refresh() {

    MetaDataDictValue resourceKeyDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("resource_keys");
    if (resourceKeyDictVal != null) {
      resourceKeys = J.get().fromJson(resourceKeyDictVal.getDictValue(),
          new TypeToken<List<String>>() {}.getType());
    }
    log.info("[resource_key] refresh keys, keys={}", resourceKeys);
  }
}
