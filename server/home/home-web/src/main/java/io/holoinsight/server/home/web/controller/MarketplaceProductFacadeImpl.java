/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.MarketplaceProductService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/marketplace/product")
public class MarketplaceProductFacadeImpl extends BaseFacade {

  @Autowired
  private MarketplaceProductService marketplaceProductService;

  @GetMapping(value = "/listAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplaceProductDTO>> listAll() {
    final JsonResult<List<MarketplaceProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<MarketplaceProductDTO> marketplaceProductDTOs =
            marketplaceProductService.findByMap(Collections.emptyMap());

        JsonResult.createSuccessResult(result, marketplaceProductDTOs);
      }
    });
    return result;
  }

}
