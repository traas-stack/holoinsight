/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import io.holoinsight.server.meta.common.util.ConstPool;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: D.java, v 0.1 2022年03月14日 10:28 上午 jinsong.yjs Exp $
 */
@Slf4j
public final class D {

  public static String getDict(String domain, String subDomain, String dictKey) {
    return Dictionaries.getInstance().getDict(domain, subDomain, dictKey);
  }

  public static String getDict(String dictKey) {
    return Dictionaries.getInstance().getDict(ConstPool.COMMON_DICT_DOMAIN,
        ConstPool.COMMON_DICT_DOMAIN, dictKey);
  }

  public static String getDict(String dictKey, String defaultValue) {
    String dict = Dictionaries.getInstance().getDict(ConstPool.COMMON_DICT_DOMAIN,
        ConstPool.COMMON_DICT_DOMAIN, dictKey);
    return dict == null ? defaultValue : dict;
  }

  public static int getInt(String dictKey, int defaultValue) {
    return getInt(ConstPool.COMMON_DICT_DOMAIN, ConstPool.COMMON_DICT_DOMAIN, dictKey,
        defaultValue);
  }

  public static int getInt(String domain, String subDomain, String dictKey, int defaultValue) {
    String num = getDict(domain, subDomain, dictKey);
    if (num == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(num);
    } catch (Exception e) {
      log.error("can not cast dict value[{},{},{},{}] to integer,use default value[{}].", domain,
          subDomain, dictKey, num, defaultValue);
      return defaultValue;
    }
  }
}
