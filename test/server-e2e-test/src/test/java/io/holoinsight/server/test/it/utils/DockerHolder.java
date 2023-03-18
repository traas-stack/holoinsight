/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import org.testcontainers.containers.DockerComposeContainer;

/**
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
public class DockerHolder {
  public static volatile DockerComposeContainer<?> DCC;
}
