/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.plugin.MarketplaceProductHandler;
import io.holoinsight.server.home.biz.service.AlertWebhookService;
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
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import com.google.gson.reflect.TypeToken;
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
@RequestMapping("/webapi/marketplace/plugin")
public class MarketplacePluginFacadeImpl extends BaseFacade {

  @Autowired
  private MarketplacePluginService marketplacePluginService;

  @Autowired
  private MarketplaceProductService marketplaceProductService;

  @Autowired
  private AlertWebhookService alarmWebhookService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private MarketplaceProductHandler marketplaceProductHandler;

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

        ParaCheckUtil.checkParaNotNull(marketplacePluginDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(marketplacePluginDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MarketplacePluginDTO item = marketplacePluginService.queryById(marketplacePluginDTO.getId(),
            ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + marketplacePluginDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(marketplacePluginDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          marketplacePluginDTO.setWorkspace(ms.getWorkspace());
        }

        updateConf(marketplacePluginDTO);

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
        userOpLogService.append("marketplace_plugin", update.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(marketplacePluginDTO),
            J.toJson(update), null, "marketplace_plugin_update");

      }
    });

    return JsonResult.createSuccessResult(true);
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
        MonitorScope ms = RequestContext.getContext().ms;
        MarketplacePluginDTO marketplacePluginDTO =
            marketplacePluginService.queryById(id, ms.getTenant(), ms.getWorkspace());

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
        MonitorScope ms = RequestContext.getContext().ms;
        MarketplacePluginDTO byId =
            marketplacePluginService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record: " + id);
        }
        marketplaceProductHandler.uninstall(byId);

        JsonResult.createSuccessResult(result, true);
        userOpLogService.append("marketplace_plugin", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "marketplace_plugin_uninstall");

      }
    });
    return result;
  }

  @GetMapping(value = "/install/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
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
        MarketplacePluginDTO marketplacePluginDTO = marketplaceProductHandler.install(byId);

        MonitorScope ms = RequestContext.getContext().ms;
        userOpLogService.append("marketplace_plugin", marketplacePluginDTO.getId(), OpType.CREATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(marketplacePluginDTO), null, null, "marketplace_plugin_install");

        JsonResult.createSuccessResult(result, marketplacePluginDTO);
      }
    });
    return result;
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
    }
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
        MonitorScope ms = RequestContext.getContext().ms;

        Map<String, Object> params = new HashMap<>();
        params.put("tenant", ms.getTenant());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          params.put("workspace", ms.getWorkspace());
        }
        List<MarketplacePluginDTO> marketplacePluginDTOs =
            marketplacePluginService.findByMap(params);
        JsonResult.createSuccessResult(result, marketplacePluginDTOs);
      }
    });
    return result;
  }
}
