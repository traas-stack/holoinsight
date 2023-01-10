/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DictLoader.java, v 0.1 2022年03月14日 10:27 上午 jinsong.yjs Exp $
 */
public interface DictLoader {

  /**
   * 加载配置
   *
   * @return
   */
  List<DictData> load() throws IOException;

  /**
   * 配置等级，支持多个loader之间覆盖 <b>相同等级之间不确定</b>
   *
   * @return
   */
  int level();

  /**
   * 定时刷新 <b>-1:不支持定时刷新</b> <b>>0:定时刷新,单位ms</b>
   *
   * @return
   */
  int timerRefresh();

}
