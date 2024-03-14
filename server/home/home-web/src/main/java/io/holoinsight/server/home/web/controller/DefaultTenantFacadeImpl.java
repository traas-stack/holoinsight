/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsDTO;
import io.holoinsight.server.home.biz.service.TenantOpsService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.TenantOpsConverter;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: DefaultTenantFacadeImpl.java, Date: 2023-04-10 Time: 17:52
 */
@RestController
@RequestMapping("/webapi/tenant")
@Slf4j
public class DefaultTenantFacadeImpl extends BaseFacade {

  @Autowired
  private TenantOpsService tenantOpsService;

  @Autowired
  private TenantOpsConverter tenantOpsConverter;

  @GetMapping(value = "/config/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> config(@PathVariable("name") String name) {
    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
        ParaCheckUtil.checkEquals(name, RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("tenant", name);
        List<TenantOps> tenantOps = tenantOpsService.listByMap(columnMap);
        if (CollectionUtils.isEmpty(tenantOps)) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }

        TenantOpsDTO tenantOpsDTO = tenantOpsConverter.doToDTO(tenantOps.get(0));

        Map<String, Object> sysMap = new HashMap<>();
        sysMap.put("workspace", null);
        if (null != tenantOpsDTO.getStorage()) {
          sysMap.put("workspace", tenantOpsDTO.getStorage().workspace);
        }
        JsonResult.createSuccessResult(result, sysMap);
      }
    });
    return result;
  }
}
