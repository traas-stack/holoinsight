/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-02-07 17:29:00
 */
@Slf4j
public abstract class AbstractResourceChecker implements LevelAuthorizationCheck {

  protected boolean checkIdNotNull(List<String> parameters) {
    if (CollectionUtils.isEmpty(parameters) || !StringUtils.isNumeric(parameters.get(0))) {
      log.error("parameters {} is empty or is not numeric.", parameters);
      return false;
    }
    return true;
  }

  protected boolean checkIdExists(List<String> parameters, String tenant, String workspace) {
    if (!checkIdNotNull(parameters)) {
      return false;
    }
    Long id = Long.parseLong(parameters.get(0));
    return checkIdExists(id, tenant, workspace);
  }

  abstract boolean checkIdExists(Long id, String tenant, String workspace);
}
