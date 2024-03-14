/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性哈希模型
 * 
 * @author jsy1001de
 * @version 1.0: ConsistentHash.java, Date: 2024-03-14 Time: 15:15
 */
public class ConsistentHash<T> {
  /**
   *
   */
  public static final int DEFAULT_NUMBER_OF_REPLICAS = 5;

  private HashFunction hashFunction = new HashFunction();
  /**
   * 虚拟节点复制比
   */
  private int numberOfReplicas = DEFAULT_NUMBER_OF_REPLICAS;
  /**
   * 存储虚拟节点hash值 到真实node的映射
   */
  private SortedMap<Long, T> circle = new TreeMap<Long, T>();

  /**
   *
   */
  public ConsistentHash() {}

  /**
   *
   */
  public ConsistentHash(Collection<T> nodes) {
    for (T node : nodes) {
      add(node);
    }
  }

  /**
   *
   */
  public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
    this(nodes);

    this.numberOfReplicas = numberOfReplicas;
  }

  /**
   * 添加节点
   *
   * @param node
   */
  public void add(T node) {
    for (int i = 0; i < numberOfReplicas; i++) {
      circle.put(hashFunction.hash(node.toString() + i), node);
    }
  }

  /**
   * 删除节点
   *
   * @param node
   */
  public void remove(T node) {
    for (int i = 0; i < numberOfReplicas; i++) {
      circle.remove(hashFunction.hash(node.toString() + i));
    }
  }

  /**
   * 获得一个最近的顺时针节点
   *
   * @param key 为给定键取Hash，取得顺时针方向上最近的一个虚拟节点对应的实际节点
   * @return
   */
  public T get(String key) {
    if (circle.isEmpty()) {
      return null;
    }
    long hash = hashFunction.hash(key);

    if (!circle.containsKey(hash)) {
      // 返回此映射的部分视图，其键大于等于 hash
      SortedMap<Long, T> tailMap = circle.tailMap(hash);
      hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
    }

    return circle.get(hash);
  }

  /**
   *
   */
  public synchronized long getSize() {
    return circle.size();
  }

}
