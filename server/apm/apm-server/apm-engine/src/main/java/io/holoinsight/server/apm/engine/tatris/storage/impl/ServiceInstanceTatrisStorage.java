/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceInstanceEsStorage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public class ServiceInstanceTatrisStorage extends ServiceInstanceEsStorage {

  @Autowired
  @Qualifier("tatrisClient")
  private RestHighLevelClient tatrisClient;

  @Override
  protected RestHighLevelClient client() {
    return tatrisClient;
  }

  @Override
  public String timeSeriesField() {
    return "_timestamp";
  }
}
