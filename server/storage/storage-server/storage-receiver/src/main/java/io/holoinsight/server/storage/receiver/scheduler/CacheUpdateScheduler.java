/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.receiver.scheduler;

import io.holoinsight.server.storage.common.utils.TimeBucket;
import io.holoinsight.server.storage.server.cache.NetworkAddressMappingCache;
import io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO;
import io.holoinsight.server.storage.server.service.NetworkAddressMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@DependsOn(value = {"networkAddressMappingCache"})
@Slf4j
public class CacheUpdateScheduler {

    @Autowired
    private NetworkAddressMappingCache networkAddressMappingCache;

    @Autowired
    private NetworkAddressMappingService networkAddressMappingService;

    @PostConstruct
    public void init() {
        log.info("Cache update scheduler start");

        final long timeInterval = 10;

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(
                        new RunnableWithExceptionProtection(() -> {
                            try {
                                update();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, t -> log
                                .error("Cache update failure.", t)), 1, timeInterval, TimeUnit.SECONDS);
    }

    private void update() throws IOException {
        updateNetAddressAliasCache();
    }

    /**
     * Update the cached data updated in last 1 minutes.
     */
    private void updateNetAddressAliasCache() throws IOException {
        long loadStartTime;
        if (networkAddressMappingCache.currentSize() == 0) {
            loadStartTime = TimeBucket.getMinuteTimeBucket(System.currentTimeMillis() - 60_000L * 60 * 24 * 10); // init 10 day
        } else {
            loadStartTime = TimeBucket.getMinuteTimeBucket(System.currentTimeMillis() - 60_000L * 10); // update 10 minute
        }
        List<NetworkAddressMappingEsDO> addressInventories = networkAddressMappingService.loadByTime(loadStartTime);

        networkAddressMappingCache.load(addressInventories);
    }

}