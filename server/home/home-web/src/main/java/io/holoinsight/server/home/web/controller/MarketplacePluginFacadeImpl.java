/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlertWebhookService;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.biz.service.MarketplacePluginService;
import io.holoinsight.server.home.biz.service.MarketplaceProductService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import com.google.gson.reflect.TypeToken;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/marketplace/plugin")
public class MarketplacePluginFacadeImpl extends BaseFacade {

  @Autowired
  private MarketplacePluginService marketplacePluginService;

  @Autowired
  private MarketplaceProductService marketplaceProductService;

  @Autowired
  private AlertWebhookService alarmWebhookService;

  @Autowired
  private ApiKeyService apiKeyService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody MarketplacePluginDTO marketplacePluginDTO) {
    final JsonResult<MarketplacePluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.id, "id");
        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.product, "product");
        ParaCheckUtil.checkParaNotBlank(marketplacePluginDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.type, "type");
        ParaCheckUtil.checkParaNotBlank(marketplacePluginDTO.json, "json");
        // ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.status);

        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(marketplacePluginDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

      }

      @Override
      public void doManage() {

        MarketplacePluginDTO item = marketplacePluginService.queryById(marketplacePluginDTO.getId(),
            RequestContext.getContext().ms.getTenant());

        if (null == item) {
          throw new MonitorException("cannot find record: " + marketplacePluginDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(marketplacePluginDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
        updateConf(marketplacePluginDTO);

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          marketplacePluginDTO.setModifier(mu.getLoginName());
        }
        marketplacePluginDTO.setGmtModified(new Date());
        marketplacePluginDTO.setStatus(true);
        marketplacePluginDTO.setTenant(MonitorCookieUtil.getTenantOrException());
        MarketplacePluginDTO update =
            marketplacePluginService.updateByRequest(marketplacePluginDTO);

        assert mu != null;
        userOpLogService.append("marketplace_plugin", String.valueOf(update.getId()), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), J.toJson(marketplacePluginDTO), J.toJson(update),
            null, "marketplace_plugin_update");

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
        MarketplacePluginDTO byId =
            marketplacePluginService.queryById(id, RequestContext.getContext().ms.getTenant());
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }

        marketplacePluginService.deleteById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("marketplace_plugin", String.valueOf(byId.getId()), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
            "marketplace_plugin_delete");

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
        List<MarketplacePluginDTO> byTypes = marketplacePluginService.findByMap(params);
        if (CollectionUtils.isEmpty(byTypes)) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + type);
        }

        List<Long> ids =
            byTypes.stream().map(MarketplacePluginDTO::getId).collect(Collectors.toList());
        for (long id : ids) {
          marketplacePluginService.deleteById(id);
        }
        JsonResult.createSuccessResult(result, null);
        for (long id : ids) {
          userOpLogService.append("marketplace_plugin", String.valueOf(id), OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(),
              RequestContext.getContext().ms.getTenant(), J.toJson(id), null, null,
              "marketplace_plugin_delete");
        }
      }
    });
    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<MarketplacePluginDTO> save(
      @RequestBody MarketplacePluginDTO marketplacePluginDTO) {
    final JsonResult<MarketplacePluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(marketplacePluginDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.product, "product");
        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.type, "type");
        ParaCheckUtil.checkParaNotBlank(marketplacePluginDTO.json, "json");
        // ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.status);
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (ms != null) {
          marketplacePluginDTO.setTenant(ms.getTenant());
        }
        if (mu != null) {
          marketplacePluginDTO.setCreator(mu.getLoginName());
          marketplacePluginDTO.setModifier(mu.getLoginName());
        }
        marketplacePluginDTO.setStatus(true);
        MarketplacePluginDTO save = marketplacePluginService.create(marketplacePluginDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("marketplace_plugin", String.valueOf(save.getId()), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), J.toJson(marketplacePluginDTO), null, null,
            "marketplace_plugin_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/queryById/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MarketplacePluginDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<MarketplacePluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MarketplacePluginDTO marketplacePluginDTO =
            marketplacePluginService.queryById(id, RequestContext.getContext().ms.getTenant());

        if (null == marketplacePluginDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }
        JsonResult.createSuccessResult(result, marketplacePluginDTO);
      }
    });
    return result;
  }

  @DeleteMapping(value = "/uninstall/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> uninstall(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MarketplacePluginDTO byId =
            marketplacePluginService.queryById(id, RequestContext.getContext().ms.getTenant());
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }
        uninstallConf(byId);

        marketplacePluginService.deleteById(id);

        JsonResult.createSuccessResult(result, true);
        userOpLogService.append("marketplace_plugin", String.valueOf(byId.getId()), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
            "marketplace_plugin_uninstall");

      }
    });
    return result;
  }

  @GetMapping(value = "/install/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MarketplacePluginDTO> install(@PathVariable("id") Long id) {
    final JsonResult<MarketplacePluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MarketplaceProductDTO byId = marketplaceProductService.findById(id);

        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }

        if (null == byId.getConfiguration()) {
          throw new MonitorException(ResultCodeEnum.OBJECT_CONVERT_ERROR, "configuration is null");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("name", byId.getName() + "_" + RequestContext.getContext().ms.getTenant());
        List<MarketplacePluginDTO> marketplacePluginDTOs =
            marketplacePluginService.findByMap(params);
        if (!CollectionUtils.isEmpty(marketplacePluginDTOs)) {
          throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL,
              "this product has installed");
        }

        MarketplacePluginDTO pluginDTO = new MarketplacePluginDTO();
        {
          pluginDTO.setStatus(true);
          pluginDTO.setTenant(RequestContext.getContext().ms.getTenant());
          pluginDTO.setCreator(RequestContext.getContext().mu.getLoginName());
          pluginDTO.setModifier(RequestContext.getContext().mu.getLoginName());
          pluginDTO.setProduct(byId.getName());
          pluginDTO.setType(byId.getType());
          pluginDTO.setName(byId.getName() + "_" + RequestContext.getContext().ms.getTenant());
          pluginDTO.setJson(installByConf(byId));
        }

        MarketplacePluginDTO marketplacePluginDTO = marketplacePluginService.create(pluginDTO);

        userOpLogService.append("marketplace_plugin", String.valueOf(marketplacePluginDTO.getId()),
            OpType.CREATE, RequestContext.getContext().mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), J.toJson(marketplacePluginDTO), null, null,
            "marketplace_plugin_install");

        JsonResult.createSuccessResult(result, marketplacePluginDTO);
      }
    });
    return result;
  }

  private String installByConf(MarketplaceProductDTO productDTO) {
    String configuration = productDTO.getConfiguration();

    List<Map<String, Object>> strings = J.toMapList(configuration);

    List<Map<String, Object>> configList = new ArrayList<>();
    for (Map<String, Object> map : strings) {
      Map<String, Object> configMap = new HashMap<>();

      if (map.get("type").equals("webhook")) {
        Map<String, Object> webhookMap = (Map<String, Object>) map.get("webhook");
        webhookMap.put("webhookName", productDTO.getName());
        webhookMap.put("tenant", RequestContext.getContext().ms.getTenant());
        webhookMap.put("gmtCreate", new Date());
        webhookMap.put("gmtModified", new Date());
        webhookMap.put("creator", RequestContext.getContext().mu.getLoginName());
        webhookMap.put("modifier", RequestContext.getContext().mu.getLoginName());
        webhookMap.put("role", "marketplace");

        AlarmWebhookDTO o =
            J.fromJson(J.toJson(webhookMap), (new TypeToken<AlarmWebhookDTO>() {}).getType());
        o.setStatus((byte) 1);
        AlarmWebhookDTO save = alarmWebhookService.save(o);

        configMap.put("type", "webhook");
        configMap.put("webhook", save);
        configList.add(configMap);
      }

      if (map.get("type").equals("dataQuery")) {

        ApiKey apiKey = new ApiKey();
        apiKey.setName(productDTO.getName());
        apiKey.setApiKey(UUID.randomUUID().toString());
        apiKey.setTenant(RequestContext.getContext().ms.getTenant());
        apiKey.setRole("marketplace");
        apiKey.setGmtCreate(new Date());
        apiKey.setGmtModified(new Date());
        apiKey.setCreator(RequestContext.getContext().mu.getLoginName());
        apiKey.setModifier(RequestContext.getContext().mu.getLoginName());
        apiKeyService.save(apiKey);
        configMap.put("type", "dataQuery");
        configMap.put("dataQuery", apiKey);
        configList.add(configMap);
      }
    }

    return J.toJson(configList);
  }

  private void uninstallConf(MarketplacePluginDTO pluginDTO) {
    String configuration = pluginDTO.getJson();

    List<Map<String, Object>> strings = J.toMapList(configuration);

    for (Map<String, Object> map : strings) {

      if (map.get("type").equals("webhook")) {

        AlarmWebhookDTO o = J.fromJson(J.toJson(map.get("webhook")),
            (new TypeToken<AlarmWebhookDTO>() {}).getType());
        alarmWebhookService.removeById(o.getId());
      }

      if (map.get("type").equals("dataQuery")) {

        ApiKey o =
            J.fromJson(J.toJson(map.get("dataQuery")), (new TypeToken<ApiKey>() {}).getType());
        apiKeyService.removeById(o.getId());
      }
    }
  }

  private void updateConf(MarketplacePluginDTO pluginDTO) {
    String configuration = pluginDTO.getJson();

    List<Map<String, Object>> strings = J.toMapList(configuration);

    for (Map<String, Object> map : strings) {

      if (map.get("type").equals("webhook")) {

        AlarmWebhookDTO o = J.fromJson(J.toJson(map.get("webhook")),
            (new TypeToken<AlarmWebhookDTO>() {}).getType());
        alarmWebhookService.updateById(o);
      }

      // if (map.get("type").equals("dataQuery")) {
      //
      // ApiKey o = J.fromJson(J.toJson(map.get("dataQuery")), (new TypeToken<ApiKey>() {
      // }).getType());
      // apiKeyService.updateById(o);
      // }
    }
  }

  @GetMapping(value = "/queryByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplacePluginDTO>> queryByName(@PathVariable("name") String name) {
    final JsonResult<List<MarketplacePluginDTO>> result = new JsonResult<>();
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
        List<MarketplacePluginDTO> marketplacePluginDTOs =
            marketplacePluginService.findByMap(params);
        JsonResult.createSuccessResult(result, marketplacePluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplacePluginDTO>> listAll() {
    final JsonResult<List<MarketplacePluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<MarketplacePluginDTO> marketplacePluginDTOs = marketplacePluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));

        JsonResult.createSuccessResult(result, marketplacePluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/list")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplacePluginDTO>> list() {
    final JsonResult<List<MarketplacePluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<MarketplacePluginDTO> marketplacePluginDTOs = marketplacePluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        JsonResult.createSuccessResult(result, marketplacePluginDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listByType/{type}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplacePluginDTO>> listByType(@PathVariable String type) {
    final JsonResult<List<MarketplacePluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("type", type);
        List<MarketplacePluginDTO> marketplacePluginDTOs =
            marketplacePluginService.findByMap(params);
        JsonResult.createSuccessResult(result, marketplacePluginDTOs);
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
        List<MarketplacePluginDTO> marketplacePluginDTOs = marketplacePluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        List<String> names = marketplacePluginDTOs == null ? null
            : marketplacePluginDTOs.stream().map(MarketplacePluginDTO::getName)
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
        List<MarketplacePluginDTO> marketplacePluginDTOs = marketplacePluginService.findByMap(
            Collections.singletonMap("tenant", MonitorCookieUtil.getTenantOrException()));
        List<String> names = marketplacePluginDTOs == null ? null
            : marketplacePluginDTOs.stream().map(MarketplacePluginDTO::getName)
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
        List<MarketplacePluginDTO> marketplacePluginDTOs =
            marketplacePluginService.findByMap(params);
        List<String> names = marketplacePluginDTOs == null ? null
            : marketplacePluginDTOs.stream().map(MarketplacePluginDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<MarketplacePluginDTO>> pageQuery(
      @RequestBody MonitorPageRequest<MarketplacePluginDTO> customPluginRequest) {
    final JsonResult<MonitorPageResult<MarketplacePluginDTO>> result = new JsonResult<>();
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
            marketplacePluginService.getListByPage(customPluginRequest));
      }
    });

    return result;
  }
}
