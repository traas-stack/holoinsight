/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core.ttl;

import io.holoinsight.server.apm.common.model.storage.Model;
import io.holoinsight.server.apm.core.ModelCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author jiwliu
 * @version : DataTtlManager.java, v 0.1 2022年10月12日 19:22 xiangwanpeng Exp $
 */
@Slf4j
public class ModelTtlManager {

  @Autowired
  private List<DataCleaner> cleaners;

  @Autowired
  @Lazy
  private ModelCenter modelManager;

  public void start() {
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
      try {
        clean();
      } catch (Exception e) {
        log.error("ttl clean fail", e);
      }
    }, 0, 1, TimeUnit.MINUTES);
  }

  private void clean() throws IOException {
    List<Model> models = modelManager.allModels();
    for (Model model : models) {
      for (DataCleaner cleaner : cleaners) {
        cleaner.clean(model);
      }
    }
  }

}
