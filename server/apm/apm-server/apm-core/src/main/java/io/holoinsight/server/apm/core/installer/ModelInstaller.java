/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core.installer;

import io.holoinsight.server.apm.common.model.storage.Model;

/**
 * @author jiwliu
 * @version : ModelInstaller.java, v 0.1 2022年10月12日 20:20 xiangwanpeng Exp $
 */
public interface ModelInstaller {

  void install(Model model);

  boolean isExists(Model model);
}
