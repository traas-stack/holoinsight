/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.ula;

import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.home.common.util.cache.local.CommonLocalCache;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.common.J;
import lombok.Data;

import java.util.concurrent.TimeUnit;

import static io.holoinsight.server.home.common.util.cache.local.CacheConst.USER_CACHE_KEY;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserCache.java, v 0.1 2022年03月15日 6:07 下午 jinsong.yjs Exp $
 */
public class UserCache {

  /**
   * 以 loginName 和 tenantId 做 key 缓存用户
   *
   * @param loginName
   * @param tenantId
   * @param mu
   */
  public void put(String loginName, String tenantId, MonitorUser mu) {
    if (mu != null) {
      UserCacheKey key = new UserCacheKey(loginName, tenantId);
      CommonLocalCache.put(USER_CACHE_KEY + "@" + MD5Hash.getMD5(J.toJson(key)), mu, 10,
          TimeUnit.MINUTES);
    }
  }

  /**
   * 以 loginName 和 tenantId 做 key 获取缓存用户
   *
   * @param loginName
   * @param tenantId
   * @return
   */
  public MonitorUser get(String loginName, String tenantId) {
    UserCacheKey key = new UserCacheKey(loginName, tenantId);
    return CommonLocalCache.get(USER_CACHE_KEY + "@" + MD5Hash.getMD5(J.toJson(key)));
  }

  @Data
  public static class UserCacheKey {
    public String loginName;
    public String tenantId;

    public UserCacheKey(String loginName, String tenantId) {
      super();
      this.loginName = loginName;
      this.tenantId = tenantId;
    }
  }
}
