/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.common;

import io.holoinsight.server.common.J;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author jsy1001de
 * @version 1.0: ResponseUtil.java, v 0.1 2022年03月15日 12:14 下午 jinsong.yjs Exp $
 */
@Slf4j
public class ResponseUtil {

  public static void response(HttpServletResponse res, Object obj) {
    if (obj == null) {
      return;
    }
    try {
      String r = J.toJson(obj);
      res.setContentType("application/json; charset=UTF-8");
      res.getWriter().write(r);
    } catch (IOException e) {
      log.error("响应JSON数据出错", e);
    }
  }

}
