/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import lombok.extern.slf4j.Slf4j;

/**
 * Registry channel cache
 * <p>
 * created at 2022/12/14
 *
 * @author xzchaoo
 */
@Slf4j
class BiStreamRegistryChannelCache {
  /**
   * The interval of evicting expired connections, in minutes.
   */
  private static final int EVICT_INTERVAL = 1;
  /**
   * Idle time of a connection before it is evicted, in minutes.
   */
  private static final int IDLE_TIME = 10;
  /**
   * Time to delay closing a connection, in minutes.
   */
  private static final int DELAY_CLOSE = 1;

  private final ConcurrentHashMap<String, CacheItem> channels = new ConcurrentHashMap<>();
  private final CommonThreadPools commonThreadPools;
  private final int port;
  private ScheduledFuture<?> scheduledFuture;

  BiStreamRegistryChannelCache(CommonThreadPools commonThreadPools, int port) {
    this.commonThreadPools = commonThreadPools;
    this.port = port;
  }

  void start() {
    scheduledFuture =
        commonThreadPools.getScheduler().scheduleWithFixedDelay(this::evictExpiredChannels, //
            EVICT_INTERVAL, EVICT_INTERVAL, TimeUnit.MINUTES);
  }

  void stop() {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    scheduledFuture = null;
    channels.values().forEach(CacheItem::shutdownNow);
    channels.clear();
  }

  Channel getChannel(String registryIp) {
    CacheItem item = getItem0(registryIp);
    item.lastAccessTime.lazySet(System.currentTimeMillis());
    return item.channel;
  }

  synchronized void evictExpiredChannels() {
    Iterator<Map.Entry<String, CacheItem>> iter = channels.entrySet().iterator();
    long expiredTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(IDLE_TIME);
    while (iter.hasNext()) {
      Map.Entry<String, CacheItem> e = iter.next();
      CacheItem item = e.getValue();
      if (item.lastAccessTime.get() < expiredTime) {
        iter.remove();
        // The channel may be used just now, so don't close it right now.
        // Just delay closing the channel.
        commonThreadPools.getScheduler().schedule(item::shutdownNow, DELAY_CLOSE, TimeUnit.MINUTES);
      }
    }
  }

  private CacheItem getItem0(String registryIp) {
    CacheItem item = channels.get(registryIp);
    if (item != null) {
      return item;
    }
    synchronized (this) {
      // double check
      item = channels.get(registryIp);
      if (item != null) {
        return item;
      }

      ManagedChannel channel = NettyChannelBuilder.forAddress(registryIp, port) //
          .usePlaintext() //
          .executor(commonThreadPools.getRpcClient()) //
          .build(); //

      item = new CacheItem(channel);
      channels.put(registryIp, item);
      return item;
    }
  }

  private static class CacheItem {
    final ManagedChannel channel;
    final AtomicLong lastAccessTime = new AtomicLong(System.currentTimeMillis());

    CacheItem(ManagedChannel channel) {
      this.channel = channel;
    }

    void shutdownNow() {
      channel.shutdownNow();
    }
  }
}
