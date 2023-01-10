/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import io.holoinsight.server.home.alert.service.task.coordinator.client.Client;
import io.holoinsight.server.common.AddressUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.alert.service.task.coordinator.AlertClusterService.HEARTBEAT_PERIOD_SECOND;
import static io.holoinsight.server.home.alert.service.task.coordinator.CoordinatorService.PORT;

/**
 * @author masaimu
 * @version 2022-10-19 18:19:00
 */
public class OrderMap {
  private static Logger LOGGER = LoggerFactory.getLogger(OrderMap.class);

  public Map<String, String> orderingMap = new HashMap<>();
  public Map<String, String> orderedMap = new HashMap<>();
  public long orderPeriod = 0;
  String selfIp = AddressUtil.getLocalHostIPV4();

  public Map<String, Integer> countMap = new HashMap<>();
  public int max = 0;
  public List<String> maxData = new ArrayList<>();
  public Map<String /* distribution */, String> distributionMap = new HashMap<>();

  Map<String, ChannelFuture> channelMap = new HashMap<>();
  Map<String, Client> clientMap = new HashMap<>();

  public static long curPeriod() {
    long cur = System.currentTimeMillis();
    long curPeriod = cur - cur % (HEARTBEAT_PERIOD_SECOND * 1000L);
    return curPeriod;
  }

  public synchronized void putOrdering(String key, String value) {
    long curPeriod = curPeriod();
    if (orderPeriod != curPeriod) {
      this.orderedMap = new HashMap<>(orderingMap);
      this.orderingMap = new HashMap<>();
      orderPeriod = curPeriod;
    }
    this.orderingMap.put(key, value);
  }

  public synchronized void count(String data) {
    long curPeriod = curPeriod();
    if (orderPeriod != curPeriod) {
      this.countMap = new HashMap<>();
      orderPeriod = curPeriod;
      max = 0;
      maxData = new ArrayList<>();
    }
    int c = this.countMap.computeIfAbsent(data, k -> 0);
    c += 1;
    if (c > max) {
      this.maxData = new ArrayList<>();
      this.maxData.add(data);
      max = c;
    } else if (c == max) {
      this.maxData.add(data);
    }
    this.countMap.put(data, c);
  }

  public synchronized void distribute(String ip, String data) {
    long curPeriod = curPeriod();
    if (orderPeriod != curPeriod) {
      this.distributionMap = new HashMap<>();
      orderPeriod = curPeriod;
    }
    this.distributionMap.put(ip, data);
  }

  public List getSortedOrderedMap() {
    List<Map.Entry<String, String>> sortedMap = new ArrayList<>(this.orderedMap.entrySet());
    sortedMap.sort(Comparator.comparing(Map.Entry::getKey));
    return sortedMap;
  }

  public Integer getRealSize() {
    int size = this.orderedMap.size();
    return size <= 0 ? 1 : size;
  }

  public Integer getRealOrder() {
    List<Map.Entry<String, String>> sortedMap = new ArrayList<>(this.orderedMap.entrySet());
    sortedMap.sort(Comparator.comparing(Map.Entry::getValue));
    for (int i = 0; i < sortedMap.size(); i++) {
      Map.Entry<String, String> entry = sortedMap.get(i);
      String ip = entry.getKey();
      if (ip.equals(selfIp)) {
        return i;
      }
    }
    LOGGER.warn("can not find myself in order map.");
    return -1;
  }

  public void addNewClient(String ip) throws Exception {

    Client client = new Client(PORT, ip);
    ChannelFuture channelFuture = client.startup();
    if (!channelFuture.isSuccess()) {
      throw new RuntimeException("no success channelFuture " + channelFuture.cause().getMessage());
    }

    this.channelMap.put(ip, channelFuture);
    this.clientMap.put(ip, client);
  }

  public boolean existActive(String ip) {
    Client client = this.clientMap.get(ip);
    if (client == null) {
      return false;
    }
    ChannelFuture channelFuture = this.channelMap.get(ip);
    if (channelFuture == null || !channelFuture.channel().isActive()) {
      client.shutdown();
      this.clientMap.remove(ip);
      this.channelMap.remove(ip);
      return false;
    }
    return true;
  }

  public void rmInactiveClient() {
    for (Map.Entry<String, ChannelFuture> entry : this.channelMap.entrySet()) {
      String ip = entry.getKey();
      ChannelFuture channelFuture = entry.getValue();
      Channel channel = channelFuture.channel();
      if (!channel.isOpen() || !channel.isActive()) {
        LOGGER.info("remove inactive endpoint {}", ip);
        Client client = this.clientMap.remove(ip);
        if (client != null) {
          client.shutdown();
        }
        this.channelMap.remove(ip);
      }
    }
  }

  public Collection<ChannelFuture> getChannels() {
    if (CollectionUtils.isEmpty(this.channelMap)) {
      return Collections.emptyList();
    }
    return this.channelMap.values();
  }
}
