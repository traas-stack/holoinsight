/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core;

import io.holoinsight.server.apm.common.model.storage.Model;
import io.holoinsight.server.apm.core.installer.IModelInstallManager;
import io.holoinsight.server.apm.core.ttl.ModelTtlManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : ModelCenter.java, v 0.1 2022年10月11日 19:31 xiangwanpeng Exp $
 */
@Slf4j
public class ModelCenter {

  @Autowired
  private IModelInstallManager modelInstallManager;

  @Autowired
  private ModelTtlManager modelTtlManager;

  private final Map<String, Model> models = new HashMap<>();

  public void init() throws IOException {
    modelInstallManager.start();
    modelTtlManager.start();
  }

  public synchronized List<Model> allModels() {
    return new ArrayList<>(this.models.values());
  }

  public synchronized void register(Model model) {
    models.put(model.getName(), model);
  }

}
