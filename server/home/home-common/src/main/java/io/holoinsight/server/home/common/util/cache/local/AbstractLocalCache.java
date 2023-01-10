/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.cache.local;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存抽象类
 * 
 * @author jsy1001de
 * @version $Id: AbstractLocalCache.java, v 0.1 2020年03月18日 07:59 jinsong.yjs Exp $
 */
public abstract class AbstractLocalCache implements LocalCache {

  // 缓存时间：单位毫秒
  private long cacheTime;
  // 创建时间：单位毫秒
  private long createTime;

  @Override
  public void refresh() {
    // 过期了刷新缓存
    long currentTime = System.currentTimeMillis();
    if (currentTime - createTime > cacheTime) {
      doRefresh();
      this.createTime = System.currentTimeMillis();
    }
  }

  public abstract void doRefresh();

  /**
   * 指定单位
   * 
   * @param cacheTime
   * @param unit
   */
  public void setCacheTime(long cacheTime, TimeUnit unit) {
    this.cacheTime = unit.toMillis(cacheTime);
  }

  /**
   * 毫秒
   * 
   * @param cacheTime
   */
  public void setCacheTime(long cacheTime) {
    this.cacheTime = cacheTime;
  }

}
