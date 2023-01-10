/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.util;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: RetryPolicy.java, v 0.1 2022年03月07日 8:51 下午 jinsong.yjs Exp $
 */
public class RetryPolicy {
  private static final Map<String, Integer> retryMsgMap = new HashMap<>();

  static {
    retryMsgMap.put("Transaction context does not exist", 1);
    retryMsgMap.put("Transaction is killed", 1);
    retryMsgMap.put("Lock wait timeout exceeded", 3);
    retryMsgMap.put("Shared lock conflict", 1);
  }

  public static int maxRetryTimes(String msg) {
    for (Map.Entry<String, Integer> entry : retryMsgMap.entrySet()) {
      if (StringUtils.contains(msg, entry.getKey()) && entry.getValue() > 0) {
        return entry.getValue();
      }
    }
    return 0;
  }

}
