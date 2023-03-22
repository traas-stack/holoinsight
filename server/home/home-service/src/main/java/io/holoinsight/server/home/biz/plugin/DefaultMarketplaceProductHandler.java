/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.AlertWebhookService;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.biz.service.MarketplacePluginService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AlarmWebhook;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author masaimu
 * @version 2023-02-20 17:56:00
 */
public class DefaultMarketplaceProductHandler implements MarketplaceProductHandler {

  @Autowired
  private MarketplacePluginService marketplacePluginService;

  @Autowired
  private AlertWebhookService alarmWebhookService;

  @Autowired
  private ApiKeyService apiKeyService;

  @Override
  public MarketplacePluginDTO install(MarketplaceProductDTO byId) {

    if (null == byId.getConfiguration()) {
      throw new MonitorException(ResultCodeEnum.OBJECT_CONVERT_ERROR, "configuration is null");
    }

    Map<String, Object> params = new HashMap<>();
    params.put("tenant", MonitorCookieUtil.getTenantOrException());
    if (StringUtils.isNotBlank(RequestContext.getContext().ms.getWorkspace())) {
      params.put("workspace", RequestContext.getContext().ms.getWorkspace());
    }
    params.put("name", byId.getName() + "_" + RequestContext.getContext().ms.getTenant());
    List<MarketplacePluginDTO> marketplacePluginDTOs = marketplacePluginService.findByMap(params);
    if (!CollectionUtils.isEmpty(marketplacePluginDTOs)) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, "this product has installed");
    }

    MarketplacePluginDTO pluginDTO = new MarketplacePluginDTO();
    {
      pluginDTO.setStatus(true);
      pluginDTO.setTenant(RequestContext.getContext().ms.getTenant());
      pluginDTO.setWorkspace(RequestContext.getContext().ms.getWorkspace());
      pluginDTO.setCreator(RequestContext.getContext().mu.getLoginName());
      pluginDTO.setModifier(RequestContext.getContext().mu.getLoginName());
      pluginDTO.setProduct(byId.getName());
      pluginDTO.setType(byId.getType());
      pluginDTO.setName(byId.getName() + "_" + RequestContext.getContext().ms.getTenant());
      pluginDTO.setJson(installByConf(byId));
    }

    MarketplacePluginDTO marketplacePluginDTO = marketplacePluginService.create(pluginDTO);
    return marketplacePluginDTO;
  }

  private String installByConf(MarketplaceProductDTO productDTO) {
    String configuration = productDTO.getConfiguration();

    List<Map<String, Object>> strings = J.toMapList(configuration);

    List<Map<String, Object>> configList = new ArrayList<>();
    for (Map<String, Object> map : strings) {
      Map<String, Object> configMap = new HashMap<>();

      if (map.get("type").equals("webhook")) {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("webhook_name", productDTO.getName());
        List<AlarmWebhook> alarmWebhooks = alarmWebhookService.listByMap(params);
        if (!CollectionUtils.isEmpty(alarmWebhooks)) {
          configMap.put("webhook", alarmWebhooks.get(0));
        } else {
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
          configMap.put("webhook", save);
        }

        configMap.put("type", "webhook");

        configList.add(configMap);
      }

      if (map.get("type").equals("dataQuery")) {
        Map<String, Object> params = new HashMap<>();
        params.put("tenant", MonitorCookieUtil.getTenantOrException());
        params.put("name", productDTO.getName());
        List<ApiKey> apiKeys = apiKeyService.listByMap(params);
        if (!CollectionUtils.isEmpty(apiKeys)) {
          configMap.put("dataQuery", apiKeys.get(0));
        } else {
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
          configMap.put("dataQuery", apiKey);
        }
        configMap.put("type", "dataQuery");
        configList.add(configMap);
      }
    }

    return J.toJson(configList);
  }

  @Override
  public Boolean uninstall(MarketplacePluginDTO byId) {
    uninstallConf(byId);
    marketplacePluginService.deleteById(byId.id);
    return true;
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
}
