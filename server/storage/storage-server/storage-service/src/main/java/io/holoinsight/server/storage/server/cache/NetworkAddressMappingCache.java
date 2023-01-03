/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.cache;

import io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * NetworkAddressMappingCache set the temporary network address - service/instance mapping in the memory cache. This data
 * was original analysis from reference of trace span.
 */
@Service
public class NetworkAddressMappingCache {
    private Cache<String, NetworkAddressMappingEsDO> networkAddressMappingCache;

    @PostConstruct
    public void init() {
        networkAddressMappingCache = CacheBuilder.newBuilder()
                                               .initialCapacity(10000)
                                               .maximumSize(1_000_000L)
                                               .build();
    }

    /**
     * @return NULL if alias doesn't exist or has been loaded in the cache.
     */
    public NetworkAddressMappingEsDO get(String address) {
        return networkAddressMappingCache.getIfPresent(address);
    }

    public void load(List<NetworkAddressMappingEsDO> NetworkAddressMappingList) {
        NetworkAddressMappingList.forEach(networkAddressMappingEsDO -> {
            networkAddressMappingCache.put(networkAddressMappingEsDO.getAddress(), networkAddressMappingEsDO);
        });
    }

    public long currentSize() {
        return networkAddressMappingCache.size();
    }
}
