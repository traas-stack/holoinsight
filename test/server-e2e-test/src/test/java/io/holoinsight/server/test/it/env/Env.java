/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.env;

import org.testcontainers.containers.DockerComposeContainer;

/**
 * <p>
 * created at 2023/3/11
 *
 * @author xzchaoo
 */
public interface Env<E extends Env> {
  String name();

  void start();

  void stop();

  String getServiceAddr(String serviceName, int servicePort);

  DockerComposeContainer<?> getCompose();

  E env(String key, String value);
}
