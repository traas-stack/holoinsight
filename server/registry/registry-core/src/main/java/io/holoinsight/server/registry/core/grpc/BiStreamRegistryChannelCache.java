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

/**
 * <p>
 * created at 2022/12/14
 *
 * @author zzhb101
 */
class BiStreamRegistryChannelCache {
  private final ConcurrentHashMap<String, CacheItem> channels = new ConcurrentHashMap<>();
  private final CommonThreadPools commonThreadPools;
  private ScheduledFuture<?> scheduledFuture;

  BiStreamRegistryChannelCache(CommonThreadPools commonThreadPools) {
    this.commonThreadPools = commonThreadPools;
  }

  void start() {
    scheduledFuture = commonThreadPools.getScheduler()
        .scheduleWithFixedDelay(this::removeExpiredChannels, 1, 1, TimeUnit.MINUTES);
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
    item.lastAccessTime.set(System.currentTimeMillis());
    return item.channel;
  }

  synchronized void removeExpiredChannels() {
    Iterator<Map.Entry<String, CacheItem>> iter = channels.entrySet().iterator();
    long expiredTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10);
    while (iter.hasNext()) {
      Map.Entry<String, CacheItem> e = iter.next();
      CacheItem item = e.getValue();
      if (item.lastAccessTime.get() < expiredTime) {
        iter.remove();
        // delay shutdown 1min
        commonThreadPools.getScheduler().schedule(item::shutdownNow, 1, TimeUnit.MINUTES);
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

      ManagedChannel channel = NettyChannelBuilder.forAddress(registryIp, 7200) //
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
