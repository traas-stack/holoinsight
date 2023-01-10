/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.cache;

import org.slf4j.Logger;

/**
 * <p>
 * SafeStringDataCache class.
 * </p>
 *
 * @author xzchaoo
 * @version $Id: SafeDataCache.java, v 0.1 2019年12月11日 19:37 jinsong.yjs Exp $
 */
public class SafeStringDataCache extends SafeDataCache<String> {

  /**
   * <p>
   * Constructor for SafeStringDataCache.
   * </p>
   */
  public SafeStringDataCache(boolean isFilterRepeat, Logger L) {
    super(isFilterRepeat, L);
  }

}
