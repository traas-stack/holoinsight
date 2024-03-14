/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableFacadeImpl.java, v 0.1 2022年03月22日 11:54 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/metatable")
public class MetaTableFacadeImpl extends BaseFacade {

  @Autowired
  private MetaTableService metaTableService;

  @GetMapping(value = "/queryByTenant")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MetaTableDTO>> queryByTenant() {
    final JsonResult<List<MetaTableDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = MonitorCookieUtil.getTenantOrException();
        List<MetaTableDTO> metaTables = metaTableService.findByTenant(tenant);
        JsonResult.createSuccessResult(result, metaTables);
      }
    });
    return result;
  }
}
