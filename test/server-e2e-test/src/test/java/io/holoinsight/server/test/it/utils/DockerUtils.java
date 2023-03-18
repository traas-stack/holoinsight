/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import java.lang.reflect.Field;

import org.testcontainers.containers.DockerComposeContainer;

import lombok.SneakyThrows;

/**
 * <p>
 * created at 2023/3/9
 *
 * @author xzchaoo
 */
public class DockerUtils {
  public static String getAddr(DockerComposeContainer<?> dcc, String serviceName, int servicePort) {
    return dcc.getServiceHost(serviceName, servicePort) + ":"
        + dcc.getServicePort(serviceName, servicePort);
  }

  public static String getHttpAddr(DockerComposeContainer<?> dcc, String serviceName,
      int servicePort) {
    return "http://" + getAddr(dcc, serviceName, servicePort);
  }

  @SneakyThrows
  public static String getProject(DockerComposeContainer<?> dcc) {
    Field field = dcc.getClass().getDeclaredField("project");
    field.setAccessible(true);
    return (String) field.get(dcc);
  }
}
