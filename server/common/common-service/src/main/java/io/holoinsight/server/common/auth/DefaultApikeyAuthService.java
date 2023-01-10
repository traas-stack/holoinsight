/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.holoinsight.server.common.BoundedSchedulers;
import io.holoinsight.server.common.dao.entity.ApikeyDO;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.xzchaoo.commons.caffeine.SafeCaffeine;

/**
 * <p>
 * created at 2022/4/18
 *
 * @author xzchaoo
 */
public class DefaultApikeyAuthService implements ApikeyAuthService {
  private final SafeCaffeine<String, AuthInfo> cache;
  @Autowired
  private ApikeyService apikeyService;

  public DefaultApikeyAuthService() {
    this.cache = new SafeCaffeine<>( //
        Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)), //
        new SafeCaffeine.F<String, AuthInfo>() {
          @Override
          public AuthInfo load(String apikey) {
            return load0(apikey);
          }

          @Override
          public void onError(String s, Throwable throwable) {

          }
        }, TimeUnit.MINUTES.toMillis(1));
  }

  private AuthInfo load0(String apikey) {
    ApikeyDO apikeyDO = apikeyService.getApikeyMap().get(apikey);
    if (apikeyDO == null) {
      throw new IllegalStateException("apikey not found " + apikey);
    }
    AuthInfo ai = new AuthInfo();
    ai.setTenant(apikeyDO.getTenant());
    return ai;
  }

  public Mono<AuthInfo> get(String apikey) {
    SafeCaffeine.CacheValue<AuthInfo> cv = cache.getCache().getIfPresent(apikey);
    if (cv != null) {
      AuthInfo v = cv.getValue();
      if (v != null) {
        return Mono.just(v);
      }
      return Mono.error(new AuthErrorException("invalid apikey [" + apikey + "]"));
    }
    return Mono.fromCallable(() -> {
      AuthInfo a = cache.get(apikey);
      if (a == null) {
        throw new AuthErrorException("invalid apikey [" + apikey + "]");
      }
      return a;
    }).subscribeOn(BoundedSchedulers.BOUNDED);
  }

  @Override
  public Mono<AuthInfo> get(String apikey, boolean cacheFirst) {
    if (cacheFirst) {
      AuthInfo a = getFromCache(apikey);
      if (a != null) {
        return Mono.just(a);
      }
    }
    return get(apikey);
  }

  @Override
  public AuthInfo getFromCache(String apikey) {
    return cache.get(apikey, false);
  }
}
