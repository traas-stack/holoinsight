/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.cache;

import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * NetworkAddressMappingCache set the temporary network address - service/instance mapping in the
 * memory cache. This data was original analysis from reference of trace span.
 */
@Service
public class NetworkAddressAliasCache {
  private Cache<String, NetworkAddressMappingDO> networkAddressMappingCache;

  @PostConstruct
  public void init() {
    networkAddressMappingCache =
        CacheBuilder.newBuilder().initialCapacity(10000).maximumSize(1_000_000L).build();
  }

  /**
   * @return NULL if alias doesn't exist or has been loaded in the cache.
   */
  public NetworkAddressMappingDO get(String address) {
    return networkAddressMappingCache.getIfPresent(address);
  }

  public void load(List<NetworkAddressMappingDO> NetworkAddressMappingList) {
    NetworkAddressMappingList.forEach(networkAddressMappingEsDO -> {
      networkAddressMappingCache.put(networkAddressMappingEsDO.getAddress(),
          networkAddressMappingEsDO);
    });
  }

  public long currentSize() {
    return networkAddressMappingCache.size();
  }
}
