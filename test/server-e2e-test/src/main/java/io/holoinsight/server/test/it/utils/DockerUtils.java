/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.shaded.org.zeroturnaround.exec.ProcessExecutor;
import org.testcontainers.shaded.org.zeroturnaround.exec.ProcessResult;

import lombok.SneakyThrows;

/**
 * <p>
 * created at 2023/3/9
 *
 * @author xzchaoo
 */
public class DockerUtils {
  public static final String COMPOSE_PROJECT_NAME = "COMPOSE_PROJECT_NAME";
  public static final String COMPOSE_FILE = "COMPOSE_FILE";

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
    return dcc.jailbreak().project;
  }

  @SneakyThrows
  public static int getPort2(DockerComposeContainer<?> dcc, String service, int originalPort) {
    String project = getProject(dcc);
    File file = dcc.jailbreak().composeFiles.get(0);

    ProcessResult result =
        new ProcessExecutor("docker-compose", "port", service, Integer.toString(originalPort)) //
            .environment(DockerUtils.COMPOSE_PROJECT_NAME, project) //
            .environment(DockerUtils.COMPOSE_FILE, file.getCanonicalPath()) //
            .readOutput(true) //
            .redirectErrorStream(true) //
            .execute(); //

    if (result.getExitValue() != 0) {
      throw new IllegalStateException("docker-compose port cmd error, code=" + result.getExitValue()
          + " output=" + result.getOutput().getUTF8());
    }
    return Integer.parseInt(StringUtils.substringAfter(result.getOutput().getUTF8(), ":").trim());
  }
}
