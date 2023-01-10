/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service;

import io.holoinsight.server.meta.facade.model.MetaType;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentHeartBeatService.java, v 0.1 2022年03月28日 2:52 下午 jinsong.yjs Exp $
 */
public interface AgentHeartBeatService {

  /**
   * 普通 vm 新增/更新
   * 
   * @param tableName
   * @param ip
   * @param hostname
   * @param row
   */
  void agentInsertOrUpdate(String tableName, String ip, String hostname, Map<String, Object> row);

  /**
   * 批量节点 新增/更新
   * 
   * @param tableName
   * @param type 类型, pod, node, container, service, ingress
   * @param row
   */
  void agentInsertOrUpdate(String tableName, MetaType type, List<Map<String, Object>> rows);

  /**
   * 节点 删除
   * 
   * @param tableName
   * @param type 类型, pod, node, container, service, ingress
   * @param row
   */
  void agentDelete(String tableName, MetaType type, List<Map<String, Object>> rows);
}
