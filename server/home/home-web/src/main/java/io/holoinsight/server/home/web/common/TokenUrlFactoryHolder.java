/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: TokenUrlFactoryHolder.java, v 0.1 2020年07月23日 5:45 下午 jinsong.yjs Exp $
 */
public class TokenUrlFactoryHolder {

  private static final Set<String> tokenUrls = Sets.newHashSet();

  public static Set<String> getTokenUrls() {
    synchronized (tokenUrls) {
      return tokenUrls;
    }
  }

  public static Boolean checkIsExist(String path) {
    synchronized (tokenUrls) {
      for (String url : tokenUrls) {
        if (path.contains(url)) {
          return true;
        }
      }
      return false;
    }
  }

  public static void setUrl(String name) {
    synchronized (tokenUrls) {
      tokenUrls.add(name);
    }
  }

}
