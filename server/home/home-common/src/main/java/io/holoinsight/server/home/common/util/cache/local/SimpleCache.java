/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.cache.local;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version $Id: SimpleCache.java, v 0.1 2020年03月18日 07:59 jinsong.yjs Exp $
 */
@Component
public class SimpleCache extends AbstractLocalCache {

  private static Map<String, Object> cacheMap = new HashMap<>();
  private static List<Object> cacheList = new ArrayList<>();

  @Override
  public void doRefresh() {
    // 清空
    cacheMap.clear();
    cacheList.clear();
    // 10分钟
    super.setCacheTime(10, TimeUnit.MINUTES);
  }
}
