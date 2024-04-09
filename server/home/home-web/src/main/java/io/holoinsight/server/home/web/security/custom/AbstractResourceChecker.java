/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-02-07 17:29:00
 */
public interface AbstractResourceChecker {

  default LevelAuthorizationCheckResult checkIdNotNull(List<String> parameters) {
    if (CollectionUtils.isEmpty(parameters)) {
      return failCheckResult("parameters %s is empty.", parameters);
    }

    if (!StringUtils.isNumeric(parameters.get(0))) {
      return failCheckResult("parameters %s is not numeric.", parameters.get(0));
    }
    return successCheckResult();
  }

  default LevelAuthorizationCheckResult checkIdExists(List<String> parameters, String tenant,
      String workspace) {
    LevelAuthorizationCheckResult checkResult = checkIdNotNull(parameters);
    if (!checkResult.isSuccess()) {
      return checkResult;
    }
    Long id = Long.parseLong(parameters.get(0));
    return checkIdExists(id, tenant, workspace);
  }

  LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace);
}
