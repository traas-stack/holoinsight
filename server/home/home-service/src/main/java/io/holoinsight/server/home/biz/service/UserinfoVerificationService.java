/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.UserinfoVerification;

/**
 * @author masaimu
 * @version 2023-06-08 21:04:00
 */
public interface UserinfoVerificationService {

  String sendMessage(UserinfoVerification userinfoVerification);
}
