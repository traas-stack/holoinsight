/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.TenantService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorEnv;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.TenantDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.MD5Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantDTOFacadeImpl.java, v 0.1 2022年09月21日 下午3:14 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/tenant")
@Slf4j
public class TenantFacadeImpl extends BaseFacade {

  @Autowired
  private TenantService tenantService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<TenantDTO> save(@RequestBody TenantDTO tenant) {
    final JsonResult<TenantDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tenant.name, "name");
        ParaCheckUtil.checkParaNotNull(tenant.code, "code");
      }

      @Override
      public void doManage() {

        if (MonitorEnv.isSaasFactoryEnv()) {
          return;
        }

        MonitorUser mu = RequestContext.getContext().mu;
        tenant.setGmtCreate(new Date());
        tenant.setGmtModified(new Date());

        tenant.setMd5(MD5Hash.getMD5(tenant.code));
        tenantService.create(tenant);

        TenantDTO byCode = tenantService.getByCode(tenant.getCode());
        JsonResult.createSuccessResult(result, tenant);

        assert mu != null;
        userOpLogService.append("tenant", String.valueOf(byCode.getId()), OpType.CREATE,
            mu.getLoginName(), tenant.getCode(), J.toJson(byCode), null, null, "tenant_create");
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody TenantDTO tenant) {
    final JsonResult<TenantDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tenant.id, "id");
        ParaCheckUtil.checkParaNotNull(tenant.name, "name");
        ParaCheckUtil.checkParaNotNull(tenant.code, "code");
      }

      @Override
      public void doManage() {
        if (MonitorEnv.isSaasFactoryEnv()) {
          return;
        }
        MonitorUser mu = RequestContext.getContext().mu;

        tenantService.update(tenant);
        TenantDTO byCode = tenantService.getByCode(tenant.getCode());
        assert mu != null;
        userOpLogService.append("tenant", String.valueOf(byCode.getId()), OpType.UPDATE,
            mu.getLoginName(), tenant.code, J.toJson(tenant), J.toJson(byCode), null,
            "tenant_update");

      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<TenantDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<TenantDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        if (MonitorEnv.isSaasFactoryEnv()) {
          return;
        }
        TenantDTO tenant = tenantService.get(id);

        if (null == tenant) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, tenant);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryAll")
  // @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<TenantDTO>> queryAll() {
    final JsonResult<List<TenantDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        if (MonitorEnv.isSaasFactoryEnv()) {
          return;
        }
        List<TenantDTO> tenantDTOList = tenantService.queryAll();

        if (null == tenantDTOList) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, tenantDTOList);
      }
    });
    return result;
  }
}
