/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.cache.local;

/**
 *
 * @author jsy1001de
 * @version $Id: LocalCacheItem.java, v 0.1 2020年03月18日 07:55 jinsong.yjs Exp $
 */
public class LocalCacheItem {

  // 缓存时间：单位毫秒
  private long cacheTime;
  // 创建时间：单位毫秒
  private long createTime;
  // 缓存值
  private Object value;

  public LocalCacheItem() {
    super();
  }

  public LocalCacheItem(long cacheTime, long createTime, Object value) {
    super();
    this.cacheTime = cacheTime;
    this.createTime = createTime;
    this.value = value;
  }

  public long getCacheTime() {
    return cacheTime;
  }

  public void setCacheTime(long cacheTime) {
    this.cacheTime = cacheTime;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
