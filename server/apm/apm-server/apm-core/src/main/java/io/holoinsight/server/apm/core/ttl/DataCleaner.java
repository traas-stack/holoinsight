/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core.ttl;

import io.holoinsight.server.apm.common.model.storage.Model;

import java.io.IOException;

/**
 * @author jiwliu
 * @version : DataTtlKeeper.java, v 0.1 2022年10月12日 19:21 xiangwanpeng Exp $
 */
public interface DataCleaner {

  void clean(Model model) throws IOException;

}
