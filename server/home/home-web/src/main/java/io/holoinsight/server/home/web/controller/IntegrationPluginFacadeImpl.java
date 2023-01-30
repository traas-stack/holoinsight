/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
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
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.registry.model.integration.GaeaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  public JsonResult<Object> update(@RequestBody IntegrationPluginDTO integrationPluginDTO) {
    final JsonResult<IntegrationPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.id, "id");
        // ParaCheckUtil.checkParaNotNull(integrationPluginDTO.product, "product");
        // ParaCheckUtil.checkParaNotBlank(integrationPluginDTO.name, "name");
        // ParaCheckUtil.checkParaNotNull(integrationPluginDTO.type, "type");
        // ParaCheckUtil.checkParaNotBlank(integrationPluginDTO.json, "json");
        // ParaCheckUtil.checkParaNotNull(integrationPluginDTO.status);

        ParaCheckUtil.checkParaNotNull(integrationPluginDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(integrationPluginDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

        IntegrationPluginDTO item = integrationPluginService.queryById(integrationPluginDTO.getId(),
            RequestContext.getContext().ms.getTenant());

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
        // integrationPluginDTO.setTenant(MonitorCookieUtil.getTenantOrException());
        IntegrationPluginDTO update =
            integrationPluginService.updateByRequest(integrationPluginDTO);

        assert mu != null;
        userOpLogService.append("integration_plugin", update.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), J.toJson(integrationPluginDTO), J.toJson(update),
            null, "integration_plugin_update");

      }
    });

    return JsonResult.createSuccessResult(true);
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
        IntegrationPluginDTO byId =
            integrationPluginService.queryById(id, RequestContext.getContext().ms.getTenant());
        if (byId == null) {
          throw new MonitorException("cannot find record: " + id);
        }

        integrationPluginService.deleteById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("integration_plugin", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
            "integration_plugin_delete");

      }
    });
    return result;
  }

  @DeleteMapping(value = "/deleteByType/{type}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> deleteByType(@PathVariable("type") String type) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(type, "type");
      }

      @Override
      public void doManage() {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("type", type);
        List<IntegrationPluginDTO> byTypes = integrationPluginService.findByMap(params);
        if (CollectionUtils.isEmpty(byTypes)) {
          throw new MonitorException("cannot find record: " + type);
        }

        List<Long> ids =
            byTypes.stream().map(IntegrationPluginDTO::getId).collect(Collectors.toList());
        for (long id : ids) {
          integrationPluginService.deleteById(id);
        }
        JsonResult.createSuccessResult(result, null);
        for (long id : ids) {
          userOpLogService.append("integration_plugin", id, OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(),
              RequestContext.getContext().ms.getTenant(), J.toJson(id), null, null,
              "integration_plugin_delete");
        }
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
        integrationPluginDTO.setStatus(true);
        IntegrationPluginDTO save = integrationPluginService.create(integrationPluginDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("integration_plugin", save.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), J.toJson(integrationPluginDTO), null, null,
            "integration_plugin_create");

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
        IntegrationPluginDTO integrationPluginDTO =
            integrationPluginService.queryById(id, RequestContext.getContext().ms.getTenant());

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
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("name", name);
        List<IntegrationPluginDTO> integrationPluginDTOs =
            integrationPluginService.findByMap(params);
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationPluginDTO>> listAll() {
    final JsonResult<List<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));

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
        List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/list/app")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationPluginDTO>> listAppPlugins() {
    final JsonResult<List<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        integrationPluginDTOs = integrationPluginDTOs.stream().filter(plugin -> {
          String type = plugin.getType();
          Class cls;
          try {
            cls = Class.forName(type);
          } catch (ClassNotFoundException e) {
            return false;
          }
          String json = plugin.getJson();
          GaeaTask gaeaTask = J.fromJson(json, cls);

          return gaeaTask instanceof LocalIntegrationTask;
        }).collect(Collectors.toList());
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listByType/{type}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationPluginDTO>> listByType(@PathVariable String type) {
    final JsonResult<List<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("type", type);
        List<IntegrationPluginDTO> integrationPluginDTOs =
            integrationPluginService.findByMap(params);
        JsonResult.createSuccessResult(result, integrationPluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAllNames")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> listAllNames() {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        List<String> names = integrationPluginDTOs == null ? null
            : integrationPluginDTOs.stream().map(IntegrationPluginDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
      }
    });
    return result;
  }

  @GetMapping(value = "/listNames")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> listNames() {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<IntegrationPluginDTO> integrationPluginDTOs = integrationPluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        List<String> names = integrationPluginDTOs == null ? null
            : integrationPluginDTOs.stream().map(IntegrationPluginDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
      }
    });
    return result;
  }

  @GetMapping(value = "/listNamesByType/{type}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> listNamesByType(@PathVariable String type) {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("type", type);
        List<IntegrationPluginDTO> integrationPluginDTOs =
            integrationPluginService.findByMap(params);
        List<String> names = integrationPluginDTOs == null ? null
            : integrationPluginDTOs.stream().map(IntegrationPluginDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<IntegrationPluginDTO>> pageQuery(
      @RequestBody MonitorPageRequest<IntegrationPluginDTO> customPluginRequest) {
    final JsonResult<MonitorPageResult<IntegrationPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          customPluginRequest.getTarget().setTenant(ms.tenant);
        }
        JsonResult.createSuccessResult(result,
            integrationPluginService.getListByPage(customPluginRequest));
      }
    });

    return result;
  }
}
