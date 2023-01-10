/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.agent;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: FileTailResponse.java, v 0.1 2022年04月24日 12:18 下午 jinsong.yjs Exp $
 */
public class FileTailResponse {

  Map<String, Object> datas = new HashMap<String, Object>();

  public void addToDatas(String key, Object value) {
    datas.put(key, value);
  }

  public Map<String, Object> getDatas() {
    return datas;
  }

  public void setDatas(Map<String, Object> datas) {
    this.datas = datas;
  }

}
