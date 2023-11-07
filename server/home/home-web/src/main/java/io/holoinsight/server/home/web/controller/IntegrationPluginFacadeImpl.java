/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/integration/plugin")
public class IntegrationPluginFacadeImpl extends BaseFacade {

  @Autowired
  private IntegrationPluginService integrationPluginService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<IntegrationPluginDTO> update(
      @RequestBody IntegrationPluginDTO integrationPluginDTO) {
    final JsonResult<IntegrationPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.id, "id");
        MonitorScope ms = RequestContext.getContext().ms;
        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(integrationPluginDTO.getTenant(), ms.getTenant(),
            "tenant is illegal");

        IntegrationPluginDTO item = integrationPluginService.queryById(integrationPluginDTO.getId(),
            ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + integrationPluginDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(integrationPluginDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          integrationPluginDTO.setModifier(mu.getLoginName());
        }
        integrationPluginDTO.setGmtModified(new Date());
        integrationPluginDTO.setStatus(true);
        integrationPluginDTO.setTenant(ms.getTenant());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          integrationPluginDTO.setWorkspace(ms.getWorkspace());
        }
        IntegrationPluginDTO update =
            integrationPluginService.updateByRequest(integrationPluginDTO);
        JsonResult.createSuccessResult(result, update);
        assert mu != null;
        userOpLogService.append("integration_plugin", update.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(integrationPluginDTO),
            J.toJson(update), null, "integration_plugin_update");

      }
    });

    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        IntegrationPluginDTO byId =
            integrationPluginService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (byId == null) {
          throw new MonitorException("cannot find record: " + id);
        }

        integrationPluginService.deleteById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("integration_plugin", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "integration_plugin_delete");

      }
    });
    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<IntegrationPluginDTO> save(
      @RequestBody IntegrationPluginDTO integrationPluginDTO) {
    final JsonResult<IntegrationPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        // ParaCheckUtil.checkParaNotBlank(integrationPluginDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.product, "product");
        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.type, "type");
        ParaCheckUtil.checkParaNotBlank(integrationPluginDTO.json, "json");
        // ParaCheckUtil.checkParaNotNull(integrationPluginDTO.status);
        ParaCheckUtil.checkParaId(integrationPluginDTO.getId());
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (ms != null) {
          integrationPluginDTO.setTenant(ms.getTenant());
        }
        if (mu != null) {
          integrationPluginDTO.setCreator(mu.getLoginName());
          integrationPluginDTO.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          integrationPluginDTO.setWorkspace(ms.workspace);
        }
        integrationPluginDTO.setStatus(true);
        IntegrationPluginDTO save = integrationPluginService.create(integrationPluginDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("integration_plugin", save.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(integrationPluginDTO),
            null, null, "integration_plugin_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/queryById/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<IntegrationPluginDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<IntegrationPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        IntegrationPluginDTO integrationPluginDTO =
            integrationPluginService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == integrationPluginDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }
        JsonResult.createSuccessResult(result, integrationPluginDTO);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationPluginDTO>> queryByName(@PathVariable("name") String name) {
    final JsonResult<List<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("product", name);
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          params.put("workspace", ms.workspace);
        }
        List<IntegrationPluginDTO> integrationPluginDTOs =
            integrationPluginService.findByMap(params);
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/list")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationPluginDTO>> list() {
    final JsonResult<List<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", ms.getTenant());

        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          params.put("workspace", ms.getWorkspace());
        }
        List<IntegrationPluginDTO> integrationPluginDTOs =
            integrationPluginService.findByMap(params);
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }
}
