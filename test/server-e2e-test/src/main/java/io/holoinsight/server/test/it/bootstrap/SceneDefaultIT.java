/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static io.holoinsight.server.test.it.bootstrap.IntegrationBootstrapHelper.prepareScene;
import static io.holoinsight.server.test.it.bootstrap.IntegrationBootstrapHelper.run0;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.AgentVMIT;
import io.holoinsight.server.test.it.AppMonitoringIT;
import io.holoinsight.server.test.it.AuthIT;
import io.holoinsight.server.test.it.LogMonitoringFolderIT;
import io.holoinsight.server.test.it.LogMonitoringIT;
import io.holoinsight.server.test.it.MetaVMIT;

/**
 * <p>
 * This class is an entry of Integration Tests(ITs). It runs all ITs serially. So it's very slow.
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
@Tag("e2e-all")
public class SceneDefaultIT extends ITSets {

  /**
   * This is a normal junit5 test method. It will call other ITs using junit5 launcher API.
   */
  @Test
  public void basic() throws Exception {
    IntegrationBootstrapHelper.run0(() -> prepareScene("scene-default"), allITs());
  }

}
