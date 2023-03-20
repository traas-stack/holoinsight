/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import io.holoinsight.server.test.it.AgentVMIT;
import io.holoinsight.server.test.it.AppMonitoringIT;
import io.holoinsight.server.test.it.AuthIT;
import io.holoinsight.server.test.it.LogMonitoringFolderIT;
import io.holoinsight.server.test.it.LogMonitoringIT;
import io.holoinsight.server.test.it.MetaVMIT;

/**
 * <p>
 * created at 2023/3/18
 *
 * @author xzchaoo
 */
public class ITSets {
  /**
   * Select all ITs.
   *
   * @return
   */
  protected static LauncherDiscoveryRequest allITs() {
    return LauncherDiscoveryRequestBuilder.request() //
        .selectors(selectClass(AuthIT.class)) //
        .selectors(selectClass(LogMonitoringFolderIT.class)) //
        .selectors(selectClass(MetaVMIT.class)) //
        .selectors(selectClass(AppMonitoringIT.class)) //
        .selectors(selectClass(AgentVMIT.class)) //
        .selectors(selectClass(LogMonitoringIT.class)) //
        .build(); //
  }
}
