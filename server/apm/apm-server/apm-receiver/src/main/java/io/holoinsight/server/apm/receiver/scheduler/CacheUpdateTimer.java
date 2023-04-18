/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.scheduler;

import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.apm.server.cache.NetworkAddressAliasCache;
import io.holoinsight.server.apm.server.service.NetworkAddressMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@DependsOn(value = {"networkAddressAliasCache"})
@Slf4j
public class CacheUpdateTimer {

  @Autowired
  private NetworkAddressAliasCache networkAddressMappingCache;

  @Autowired
  private NetworkAddressMappingService networkAddressMappingService;

  @PostConstruct
  public void init() {
    log.info("Cache update scheduler start");

    final long timeInterval = 10;

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(new RunnableWithExceptionProtection(() -> {
          try {
            update();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }, t -> log.error("Cache update failure.", t)), 1, timeInterval, TimeUnit.SECONDS);
  }

  private void update() throws Exception {
    updateNetAddressAliasCache();
  }

  private void updateNetAddressAliasCache() throws Exception {
    long loadStartTime;
    if (networkAddressMappingCache.currentSize() == 0) {
      loadStartTime = System.currentTimeMillis() - 60_000L * 60 * 24 * 3; // init
                                                                          // 3
                                                                          // day
    } else {
      loadStartTime = System.currentTimeMillis() - 60_000L * 10; // update
                                                                 // 10
                                                                 // minute
    }
    List<NetworkAddressMappingDO> addressMapping =
        networkAddressMappingService.loadByTime(loadStartTime);

    networkAddressMappingCache.load(addressMapping);
  }

}
