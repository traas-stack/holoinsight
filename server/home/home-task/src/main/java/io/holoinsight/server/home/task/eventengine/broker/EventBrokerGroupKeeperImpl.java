/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

import io.holoinsight.server.home.task.eventengine.event.ConsistentHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: EventBrokerGroupKeeperImpl.java, Date: 2024-03-14 Time: 15:13
 */
@Slf4j
public class EventBrokerGroupKeeperImpl
    implements EventBrokerGroupKeeper, EventBrokerGroupCallback {

  private final ConsistentHash<String> consistentHash = new ConsistentHash<>();

  private final Set<String> brokerServerAddresses = new HashSet<>();

  private final Set<String> leavedGroupMembersCache = new HashSet<>();

  private final Set<String> joinedGroupMembersCache = new HashSet<>();

  private EventBrokerGroupObserver eventBrokerGroupObserver;

  private EventBrokerGroupPublisher eventBrokerGroupPublisher;

  @Override
  public void updateBrokerGroup(Set<String> groupMembers) {
    synchronized (brokerServerAddresses) {
      if (!brokerServerAddresses.equals(groupMembers)) {
        refreshGroup(groupMembers);
      }
    }
  }

  @Override
  public void register() {
    eventBrokerGroupObserver.watch(this);
    eventBrokerGroupPublisher.publish();
  }

  @Override
  public String choseProperMember(String key) {
    return consistentHash.get(key);
  }

  @Override
  public List<String> getMembers() {
    List<String> servers;

    synchronized (brokerServerAddresses) {
      servers = new ArrayList<>(brokerServerAddresses);
    }

    Collections.sort(servers);

    return servers;
  }

  @Override
  public List<String> pollLeavedMembers() {
    List<String> leavedGroupMembers;

    synchronized (leavedGroupMembersCache) {
      leavedGroupMembers = new ArrayList<>(leavedGroupMembersCache);
      leavedGroupMembersCache.clear();
    }

    Collections.sort(leavedGroupMembers);

    return leavedGroupMembers;
  }

  @Override
  public List<String> pollJoinedMembers() {
    List<String> joinedGroupMembers;

    synchronized (joinedGroupMembersCache) {
      joinedGroupMembers = new ArrayList<>(joinedGroupMembersCache);
      joinedGroupMembersCache.clear();
    }

    Collections.sort(joinedGroupMembers);

    return joinedGroupMembers;
  }

  private void refreshGroup(final Set<String> groupMembers) {
    Set<String> latestBrokerServerAddresses = new HashSet<>(groupMembers);
    Set<String> joinedServers;
    Set<String> leavedServers;

    synchronized (brokerServerAddresses) {
      joinedServers = new HashSet<>(latestBrokerServerAddresses);
      leavedServers = new HashSet<>(brokerServerAddresses);
      joinedServers.removeAll(brokerServerAddresses);
      leavedServers.removeAll(latestBrokerServerAddresses);

      for (String addedServer : joinedServers) {
        // 对于新加入的broker节点，将其加入环
        // 新加入的broker节点不会影响加入前已有节点的负载
        consistentHash.add(addedServer);
        brokerServerAddresses.add(addedServer);
      }

      for (String removedServer : leavedServers) {
        // 对于离开group的broker节点，将其从环中移走
        consistentHash.remove(removedServer);
        brokerServerAddresses.remove(removedServer);
      }
    }

    if (!leavedServers.isEmpty()) {
      log.warn("[GROUP CHANGED]Event service instance "
          + StringUtils.join(new ArrayList<>(leavedServers)) + " left group!");

      synchronized (leavedGroupMembersCache) {
        // 缓存处理减轻并发压力
        leavedGroupMembersCache.addAll(leavedServers);
      }
    }

    if (!joinedServers.isEmpty()) {
      log.info("New service instance " + StringUtils.join(new ArrayList(joinedServers))
          + " just joined");

      synchronized (joinedGroupMembersCache) {
        joinedGroupMembersCache.addAll(joinedServers);
      }
    }
  }
}
