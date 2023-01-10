/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.access.MonitorAccessService;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.controller.model.TokenQueryRequest;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jsy1001de
 * @version 1.0: TokenQueryFacadeImpl.java, v 0.1 2022年06月14日 2:12 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/token/apply")
public class TokenQueryFacadeImpl extends BaseFacade {

  @Autowired
  private MonitorAccessService monitorAccessService;

  @PostMapping
  public JsonResult<String> query(@RequestBody TokenQueryRequest request) {

    final JsonResult<String> result = new JsonResult<>();


    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.accessId, "accessId");
        ParaCheckUtil.checkParaNotNull(request.accessKey, "accessKey");
      }

      @Override
      public void doManage() {
        String apply = monitorAccessService.apply(request.accessId, request.accessKey);
        JsonResult.createSuccessResult(result, apply);
      }
    });

    return result;
  }
}
