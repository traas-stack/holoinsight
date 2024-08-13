/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.holoinsight.server.common.dao.mapper.GaeaCollectConfigDOMapper;

/**
 * <p>
 * created at 2022/4/19
 *
 * @author zzhb101
 */
@Service
public class TemplateService {
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private GaeaCollectConfigDOMapper mapper;

  public CollectTemplate fuzzyGet(String t) {
    try {
      return templateStorage.get(Long.parseLong(t));
    } catch (NumberFormatException ignored) {
      // This catch statement is intentionally empty
    }

    Set<Long> ids = templateStorage.get(t);
    if (ids == null) {
      return null;
    }
    switch (ids.size()) {
      case 0:
        break;
      case 1:
        return templateStorage.get(ids.iterator().next());
      default:
        throw new IllegalStateException("template not found " + t);
    }

    //

    return null;
  }
}
