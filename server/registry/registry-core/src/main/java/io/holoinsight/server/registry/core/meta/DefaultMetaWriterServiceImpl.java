/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.registry.core.meta;

import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: DefaultMetaWriterServiceImpl.java, Date: 2023-04-06 Time: 18:02
 */
public class DefaultMetaWriterServiceImpl implements MetaWriterService {
  @Override
  public Map<String, Object> getExtraLabel(Map<String, String> labels) {
    return null;
  }
}
