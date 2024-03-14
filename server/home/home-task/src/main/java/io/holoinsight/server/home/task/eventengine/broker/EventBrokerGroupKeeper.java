/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: EventBrokerGroupKeeper.java, Date: 2024-03-14 Time: 15:13
 */
public interface EventBrokerGroupKeeper {
  /**
   * 将当前broker注册到broker集群中
   */
  void register();

  /**
   * 根据指定的key选择适合处理的broker
   *
   * @param key
   * @return
   */
  String choseProperMember(String key);

  /**
   * 得到成员列表
   *
   * @return
   */
  List<String> getMembers();

  /**
   * 得到并清除退出broker集群的broker节点
   *
   * @return
   */
  List<String> pollLeavedMembers();

  /**
   * 得到并清除新加入broker集群的broker节点
   *
   * @return
   */
  List<String> pollJoinedMembers();
}
