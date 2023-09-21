/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * <p>
 * created at 2023/2/10
 *
 * @author xzchaoo
 */
public class SSRFUtils {
  protected static final List<SSRFHook> hooks = new ArrayList<>();

  static {
    ClassLoader classLoaderToUser = SSRFUtils.class.getClassLoader();

    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
    if (tccl != null) {
      classLoaderToUser = tccl;
    }

    ServiceLoader.load(SSRFHook.class, classLoaderToUser).forEach(hooks::add);
  }

  public static void hookStart() {
    for (int i = 0; i < hooks.size(); i++) {
      hooks.get(i).start();
    }
  }

  public static Boolean hookCheckUrl(String url) {
    for (int i = 0; i < hooks.size(); i++) {
      Boolean aBoolean = hooks.get(i).checkUrl(url);
      if (!aBoolean)
        return false;
    }
    return true;
  }

  public static void hookStop() {
    for (int i = hooks.size() - 1; i >= 0; i--) {
      hooks.get(i).stop();
    }
  }
}
