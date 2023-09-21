/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.common.JsonResult;

/**
 * facade 请求模版
 * 
 * @author jsy1001de
 * @version 1.0: ManageTemplate.java, v 0.1 2022年03月15日 12:21 下午 jinsong.yjs Exp $
 */
public interface FacadeTemplate {
  void manage(JsonResult result, ManageCallback callback);

  void manage(JsonResult result, ManageCallback callback, String trace);

}
