/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import lombok.Data;

/**
 * @author masaimu
 * @version 2024-03-25 17:29:00
 */
@Data
public class LevelAuthorizationCheckResult {
  private boolean success;
  private String errorMsg;

  public static LevelAuthorizationCheckResult successCheckResult() {
    LevelAuthorizationCheckResult checkResult = new LevelAuthorizationCheckResult();
    checkResult.setSuccess(true);
    return checkResult;
  }

  public static LevelAuthorizationCheckResult failCheckResult(String errorMsg) {
    LevelAuthorizationCheckResult checkResult = new LevelAuthorizationCheckResult();
    checkResult.setSuccess(false);
    checkResult.setErrorMsg(errorMsg);
    return checkResult;
  }

  public static LevelAuthorizationCheckResult failCheckResult(String format, Object... args) {
    LevelAuthorizationCheckResult checkResult = new LevelAuthorizationCheckResult();
    checkResult.setSuccess(false);
    checkResult.setErrorMsg(String.format(format, args));
    return checkResult;
  }
}
