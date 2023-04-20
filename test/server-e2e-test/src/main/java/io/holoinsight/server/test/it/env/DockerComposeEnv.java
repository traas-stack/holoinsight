/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.env;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.shaded.org.zeroturnaround.exec.ProcessExecutor;
import org.testcontainers.shaded.org.zeroturnaround.exec.ProcessResult;

import io.holoinsight.server.test.it.utils.DockerUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/3/11
 *
 * @author xzchaoo
 */
@Slf4j
public class DockerComposeEnv implements Env<DockerComposeEnv> {
  private final String name;
  private DockerComposeContainer<?> dcc;
  private File composeFile;
  private Map<String, String> envs = new HashMap<>();

  public DockerComposeEnv(String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public String name() {
    return name;
  }

  @SneakyThrows
  @Override
  public void start() {
    composeFile = new File( //
        new File(".").getAbsolutePath(), //
        "../scenes/" + name + "/docker-compose.yaml") //
            .getCanonicalFile(); //
    dcc = new DockerComposeContainer<>(composeFile) //
        .withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL) //
        .withBuild(true) //
        .withLocalCompose(true) //
        .withExposedService("server", 80); //

    this.envs.forEach(dcc::withEnv);

    // Pass envs starting with COMPOSE_ to the docker-compose.
    System.getenv().forEach((k, v) -> {
      if (DockerUtils.COMPOSE_PROJECT_NAME.equals(k) || DockerUtils.COMPOSE_FILE.equals(k)) {
        return;
      }
      if (k.startsWith("COMPOSE_")) {
        String trimmedKey = k.substring("COMPOSE_".length());
        dcc.withEnv(trimmedKey, v);
      }
    });

    long begin = System.currentTimeMillis();
    log.info("docker-compose up file={}", composeFile.getAbsolutePath());
    try {
      dcc.start();
      long cost = System.currentTimeMillis() - begin;
      log.info("docker-compose bootstrap success, cost=[{}]ms", cost);
    } finally {
      try {
        new ProcessExecutor("free", "-m") //
            .redirectOutput(System.out) //
            .redirectError(System.err) //
            .timeout(10, TimeUnit.MINUTES) //
            .execute();
      } catch (IOException e) {
        log.error("free -m error", e);
      }

      try {
        new ProcessExecutor("docker", "ps", "-a") //
            .redirectOutput(System.out) //
            .redirectError(System.err) //
            .timeout(10, TimeUnit.MINUTES) //
            .execute();
      } catch (IOException e) {
        log.error("docker ps -a", e);
      }
    }


    File after = new File(composeFile.getParentFile(), "after.sh");
    if (after.exists() && after.canExecute()) {
      String afterPath = after.getAbsolutePath();
      log.info("found {} executable", afterPath);

      String project = DockerUtils.getProject(dcc);

      ProcessResult result = new ProcessExecutor(afterPath) //
          .environment(DockerUtils.COMPOSE_PROJECT_NAME, project) //
          .readOutput(true) //
          .timeout(10, TimeUnit.MINUTES) //
          .execute(); //

      log.info("execute {} code=[{}] stdout:\n{}", afterPath, result.getExitValue(),
          result.getOutput().getUTF8());
      if (result.getExitValue() != 0) {
        throw new IllegalStateException("fail to execute " + afterPath);
      }
    }

  }

  @Override
  public void stop() {
    if (dcc != null) {
      dcc.stop();
    }
  }

  @Override
  public String getServiceAddr(String serviceName, int servicePort) {
    return DockerUtils.getAddr(dcc, serviceName, servicePort);
  }

  @Override
  public DockerComposeContainer<?> getCompose() {
    return dcc;
  }

  @Override
  public DockerComposeEnv env(String key, String value) {
    envs.put(key, value);
    return this;
  }
}
