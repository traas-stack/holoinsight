/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.cache.local;

import io.holoinsight.server.common.config.ScheduleLoadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 本地缓存管理器
 * 
 * @author jsy1001de
 * @version $Id: LocalCacheManage.java, v 0.1 2020年03月18日 07:56 jinsong.yjs Exp $
 */
@Component
@Slf4j
public class LocalCacheManage extends ScheduleLoadTask {

  @Autowired
  private List<LocalCache> localCaches;

  /**
   * 应用启动初始化
   */

  @Override
  public void load() throws Exception {
    if (localCaches != null && !localCaches.isEmpty()) {
      for (LocalCache cache : localCaches) {
        try {
          cache.refresh();
        } catch (Exception e) {
          log.error("本地缓存更新异常", e);
        }
      }
    }
  }

  @Override
  public int periodInSeconds() {
    return 60;
  }

  @Override
  public String getTaskName() {
    return "LocalCacheManage";
  }
}
