/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import io.holoinsight.server.meta.common.util.ConstPool;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: RuntimeDictLoader.java, v 0.1 2022年03月14日 10:26 上午 jinsong.yjs Exp $
 */
@Slf4j
class RuntimeDictLoader implements DictLoader {

  @Override
  public List<DictData> load() {
    List<DictData> datas = new LinkedList<>();
    System.getProperties().forEach((k, v) -> {
      if (k instanceof String) {
        if (((String) k).startsWith("metaservice.")) {
          datas.add(new DictData(level(), ConstPool.COMMON_DICT_DOMAIN,
              ConstPool.COMMON_DICT_DOMAIN, (String) k, null, (String) v));
          log.info("RuntimeDictLoader load property, {}={}.", k, v);
        }
      }
    });
    return datas;
  }

  /**
   * 启动参数一般不可以修改
   *
   * @return
   */
  @Override
  public int level() {
    return 100;
  }

  @Override
  public int timerRefresh() {
    return -1;
  }
}
