/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.env;

import org.testcontainers.containers.DockerComposeContainer;

/**
 * <p>
 * created at 2023/3/13
 *
 * @author xzchaoo
 */
public class NoopEnv implements Env<NoopEnv> {
  @Override
  public String name() {
    return "noop";
  }

  @Override
  public void start() {

  }

  @Override
  public void stop() {

  }

  @Override
  public String getServiceAddr(String serviceName, int servicePort) {
    return null;
  }

  @Override
  public DockerComposeContainer<?> getCompose() {
    return null;
  }

  @Override
  public NoopEnv env(String key, String value) {
    return this;
  }
}
