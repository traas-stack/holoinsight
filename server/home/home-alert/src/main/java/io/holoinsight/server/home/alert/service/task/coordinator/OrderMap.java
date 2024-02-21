/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.DateUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.service.task.coordinator.client.Client;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.alert.service.task.coordinator.CoordinatorService.PORT;

/**
 * @author masaimu
 * @version 2022-10-19 18:19:00
 */
@Slf4j
@Data
public class OrderMap {

  private Map<String, String> orderingMap = new HashMap<>();
  private Map<String, String> orderedMap = new HashMap<>();
  private long orderPeriod = 0;
  private final String selfIp = AddressUtil.getLocalHostIPV4();

  private boolean forceSetEnable = false;
  private Map<String, String> forceOrderedMap = new HashMap<>();

  public Map<String, Integer> countMap = new HashMap<>();
  public int max = 0;
  public List<String> maxData = new ArrayList<>();
  public Map<String /* distribution */, String> distributionMap = new HashMap<>();

  Map<String, ChannelFuture> channelMap = new HashMap<>();
  Map<String, Client> clientMap = new HashMap<>();

  public static long curPeriod() {
    return PeriodType.TEN_SECOND.rounding(System.currentTimeMillis());
  }

  public synchronized void putOrdering(String key, String value) {
    long curPeriod = curPeriod();
    if (orderPeriod != curPeriod) {
      log.info("ORDER_MONITOR,{},forceEnable={},forceOrderedMapSize={},orderingMapSize={}",
          DateUtil.getDateOf_YYMMDD_HHMMSS(new Date(curPeriod)), forceSetEnable,
          forceOrderedMap.size(), orderingMap.size());
      if (forceSetEnable) {
        this.orderedMap = new HashMap<>(forceOrderedMap);
      } else {
        this.orderedMap = new HashMap<>(orderingMap);
      }
      this.orderingMap = new HashMap<>();
      orderPeriod = curPeriod;
    }
    this.orderingMap.put(key, value);
  }

  public synchronized void count(String data) {
    long curPeriod = curPeriod();
    if (orderPeriod != curPeriod) {
      this.countMap = new HashMap<>();
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
    }
    this.distributionMap.put(ip, data);
  }

  public List getSortedOrderedMap() {
    List<Map.Entry<String, String>> sortedMap = new ArrayList<>(this.orderedMap.entrySet());
    sortedMap.sort(Comparator.comparing(Map.Entry::getKey));
    return sortedMap;
  }

  public Integer getRealSize() {
    return this.orderedMap.size();
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
    log.warn("can not find myself in order map {} {}.", selfIp, J.toJson(sortedMap));
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
        log.info("remove inactive endpoint {}", ip);
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

  public void forceSet(OrderConfig orderConfig) {
    if (orderConfig.isEnable() && CollectionUtils.isEmpty(orderConfig.getOrderedMap())) {
      log.info("OrderedMapConfig is enable but empty.");
      return;
    }
    this.forceSetEnable = orderConfig.isEnable();
    this.forceOrderedMap = new HashMap<>(orderConfig.getOrderedMap());
  }
}
