/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
public interface ApikeyAuthService {
  Mono<AuthInfo> get(String apikey);

  Mono<AuthInfo> get(String apikey, boolean cacheFirst);

  AuthInfo getFromCache(String apikey);
}
