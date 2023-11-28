/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

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
      log.error("json error", e);
    }
  }

  public static void authFailedResponse(HttpServletResponse resp, int status, String errorMsg,
      ResultCodeEnum resultCodeEnum) throws IOException {
    resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
    resp.setStatus(status);
    JsonUtils.writeValue(resp.getWriter(),
        JsonResult.createFailResult(errorMsg, resultCodeEnum.getResultCode()));
  }

}
