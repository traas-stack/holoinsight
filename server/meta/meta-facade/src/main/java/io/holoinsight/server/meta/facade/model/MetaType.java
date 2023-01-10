/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.model;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaType.java, v 0.1 2022年06月30日 2:07 下午 jinsong.yjs Exp $
 */
public enum MetaType {

  VM,

  POD,

  NODE,

  CONTAINER,

  SERVICE,

  INGRESS,

  // 我们的agent, 包含 daemonset clusteragent central
  AGENT
}
