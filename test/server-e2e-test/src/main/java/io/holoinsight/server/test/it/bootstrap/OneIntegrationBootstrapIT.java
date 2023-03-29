/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static io.holoinsight.server.test.it.bootstrap.IntegrationBootstrapHelper.prepareScene;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class is an entry of Integration Tests(ITs) when running in Github Actions. With 'Matrix'
 * ability of Github Actions, we can run all ITs concurrently. This class runs only one IT when it
 * is called.
 * <p>
 * This class is annotated with {@link Tag} 'e2e-one' so it can be called easily by
 * '-Dgroups=e2e-one'.
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
@Tag("e2e-one")
@Slf4j
public class OneIntegrationBootstrapIT {
  @Test
  public void test() throws Exception {
    String className = System.getenv("IT_CLASS");
    if (StringUtils.isEmpty(className)) {
      throw new IllegalArgumentException("IT_CLASS env is required");
    }
    // Prepend className prefix if missing.
    if (!className.startsWith("io.holoinsight.")) {
      className = "io.holoinsight.server.test.it." + className;
    }

    String scene;
    {
      String scene0 = System.getenv("IT_SCENE");
      if (StringUtils.isEmpty(scene0)) {
        scene0 = "scene-default";
      }
      scene = scene0;
    }

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request() //
        .selectors(selectClass(className)) //
        .build(); //

    log.info("run IT [{}]", className);
    IntegrationBootstrapHelper.run0(() -> prepareScene(scene), request);
  }
}
