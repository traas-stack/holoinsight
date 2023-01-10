/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClientService.java, v 0.1 2022年03月07日 5:13 下午 jinsong.yjs Exp $
 */
public interface ClientService {

  String getCurrentApp();

  String getLocalIp();

  String getDomain();

  List<String> getCacheServers();

  String getCacheServer();

  /**
   * 是否透传至服务端模式
   *
   * @return
   */
  boolean isRemote();
}
