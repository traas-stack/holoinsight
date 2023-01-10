/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.cache;

import org.slf4j.Logger;

/**
 * <p>
 * SafeObjectDataCache class.
 * </p>
 *
 * @author xzchaoo
 * @version $Id: SafeObjectDataCache.java, v 0.1 2019年12月11日 19:37 jinsong.yjs Exp $
 */
public class SafeObjectDataCache extends SafeDataCache<Object> {

  /**
   * <p>
   * Constructor for SafeObjectDataCache.
   * </p>
   */
  public SafeObjectDataCache(boolean isFilterRepeat, Logger L) {
    super(isFilterRepeat, L);
  }
}
