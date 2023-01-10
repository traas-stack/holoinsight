/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import lombok.extern.slf4j.Slf4j;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author jsy1001de
 * @version 1.0: Dictionaries.java, v 0.1 2022年03月14日 10:25 上午 jinsong.yjs Exp $
 */
@Slf4j
public class Dictionaries {

  private Map<String, DictData> values = new ConcurrentHashMap<>();
  private Map<String, ConcurrentHashMap<DictHook, DictHook>> hooks = new ConcurrentHashMap<>();

  private static final Dictionaries INSTANCE = new Dictionaries();

  public static Dictionaries getInstance() {
    return INSTANCE;
  }

  private Dictionaries() {
    // 初始化启动配置
    // registerLoader(new RuntimeDictLoader());
    // //初始化基础配置文件
    // registerLoader(new PropertiesDictLoader(0));
  }

  private Set<DictLoader> loaders = new HashSet<>();

  private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(5);

  public void registerLoader(DictLoader loader) {
    if (loaders.contains(loader)) {
      log.warn("dictLoader[{}] load conflict.", loader.getClass().getSimpleName());
      return;
    }
    log.info("register dictLoader[{},{},{}].", loader.getClass().getSimpleName(), loader.level(),
        loader.timerRefresh());
    try {
      refresh(loader.load());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (loader.timerRefresh() > 0) {
      AtomicBoolean atomic = new AtomicBoolean(false);
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        if (atomic.compareAndSet(false, true)) {
          try {
            refresh(loader.load());
          } catch (Exception e) {
            log.error("load dict[" + loader.getClass().getSimpleName() + "] fail.", e);
          } finally {
            atomic.compareAndSet(true, false);
          }
        }
      }, 0, loader.timerRefresh(), TimeUnit.MILLISECONDS);
    }
    this.loaders.add(loader);
  }

  public void registerHook(String domain, String subDomain, String dictKey, DictHook hook) {
    hooks.computeIfAbsent(toKey(domain, subDomain, dictKey), k -> new ConcurrentHashMap<>())
        .put(hook, hook);
  }

  public void unregisterHook(String domain, String subDomain, String dictKey, DictHook hook) {
    hooks.computeIfAbsent(toKey(domain, subDomain, dictKey), k -> new ConcurrentHashMap<>())
        .remove(hook, hook);
  }

  public void refresh(List<DictData> dictDatas) {
    for (DictData dictData : dictDatas) {
      String identity = toKey(dictData.getDomain(), dictData.getSubDomain(), dictData.getDictKey());
      values.compute(identity, (key, origin) -> {
        if (origin == null) {
          origin = dictData;
          log.info("add dict[{},{},{}].", origin.getPriority(), key, origin.getDictValue());
        } else if (origin.getPriority() <= dictData.getPriority()
            && !origin.getDictValue().equals(dictData.getDictValue())) {
          final DictData or = origin;
          for (DictHook hook : hooks.computeIfAbsent(identity, k -> new ConcurrentHashMap<>())
              .values()) {
            scheduledExecutorService.execute(() -> {
              try {
                hook.update(or.getDictValue(), dictData.getDictValue());
              } catch (Exception e) {
                log.error("execute dict hook[" + identity + "] fail.", e);
              }
            });
          }
          origin = dictData;
          log.info("update dict[{},{},{}].", origin.getPriority(), key, origin.getDictValue());
        }
        return origin;
      });
    }
  }

  private String toKey(String domain, String subDomain, String dictKey) {
    return String.format("%s#%s#%s", domain, subDomain, dictKey);
  }

  public String getDict(String domain, String subDomain, String dictKey) {
    DictData v = values.get(toKey(domain, subDomain, dictKey));
    return v == null ? null : v.getDictValue();
  }
}
