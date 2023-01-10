/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.cache;

import java.util.Map;

import org.slf4j.Logger;

/**
 * <p>
 * SafeMapDataCache class.
 * </p>
 *
 * @author xzchaoo
 * @version $Id: SafeDataCache.java, v 0.1 2019年12月11日 19:37 jinsong.yjs Exp $
 */
public class SafeMapDataCache extends SafeDataCache<Map<String, Object>> {

  /**
   * <p>
   * Constructor for SafeMapDataCache.
   * </p>
   */
  public SafeMapDataCache(boolean isFilterRepeat, Logger L) {
    super(isFilterRepeat, L);
  }
}
