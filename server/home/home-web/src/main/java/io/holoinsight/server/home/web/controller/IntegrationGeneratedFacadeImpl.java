/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.home.biz.plugin.PluginRepository;
import io.holoinsight.server.home.biz.plugin.config.LogPluginConfig;
import io.holoinsight.server.home.biz.plugin.core.AbstractIntegrationPlugin;
import io.holoinsight.server.home.biz.plugin.core.LogPlugin;
import io.holoinsight.server.home.biz.plugin.model.Plugin;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import io.holoinsight.server.home.biz.service.IntegrationGeneratedService;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.common.service.UserOpLogService;
import io.holoinsight.server.common.MonitorException;
import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationConfigDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationFormDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Date: 2023-03-13 Time: 18:35
 * </p>
 *
 * @author jsy1001de
 */
@RestController
@RequestMapping("/webapi/integrationGenerated")
@Slf4j
public class IntegrationGeneratedFacadeImpl extends BaseFacade {

  @Autowired
  private IntegrationGeneratedService integrationGeneratedService;

  @Autowired
  private IntegrationPluginService integrationPluginService;

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private PluginRepository pluginRepository;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private TenantInitService tenantInitService;


  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<IntegrationGeneratedDTO> update(
      @RequestBody IntegrationGeneratedDTO generatedDTO) {
    final JsonResult<IntegrationGeneratedDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(generatedDTO.id, "id");
        ParaCheckUtil.checkParaNotNull(generatedDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(generatedDTO.item, "item");
        ParaCheckUtil.checkParaNotNull(generatedDTO.product, "product");
        ParaCheckUtil.checkParaNotNull(generatedDTO.config, "config");
        ParaCheckUtil.checkParaNotNull(generatedDTO.custom, "custom");

        MonitorScope ms = RequestContext.getContext().ms;
        IntegrationGeneratedDTO item = integrationGeneratedService.queryById(generatedDTO.getId(),
            tenantInitService.getTraceTenant(ms.getTenant()), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + generatedDTO.getId());
        }
        ParaCheckUtil.checkEquals(item.getTenant(),
            tenantInitService.getTraceTenant(ms.getTenant()), "tenant is illegal");

      }

      @Override
      public void doManage() {

        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;
        generatedDTO.setWorkspace(ms.getWorkspace());
        generatedDTO.setTenant(tenantInitService.getTraceTenant(ms.getTenant()));
        generatedDTO.setModifier(mu.getLoginName());
        generatedDTO.setGmtModified(new Date());
        generatedDTO.setDeleted(false);
        integrationGeneratedService.update(generatedDTO);
        IntegrationGenerated byId = integrationGeneratedService.getById(generatedDTO.getId());
        userOpLogService.append("integration_generated", byId.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(generatedDTO),
            J.toJson(byId), null, "integration_generated_update");
        JsonResult.createSuccessResult(result, generatedDTO);
      }
    });

    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<IntegrationGeneratedDTO> create(
      @RequestBody IntegrationGeneratedDTO generatedDTO) {
    final JsonResult<IntegrationGeneratedDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(generatedDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(generatedDTO.item, "item");
        ParaCheckUtil.checkParaNotNull(generatedDTO.product, "product");
        ParaCheckUtil.checkParaNotNull(generatedDTO.config, "config");
        ParaCheckUtil.checkParaNotNull(generatedDTO.custom, "custom");
        ParaCheckUtil.checkParaId(generatedDTO.getId());

      }

      @Override
      public void doManage() {

        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;
        generatedDTO.setWorkspace(ms.getWorkspace());
        generatedDTO.setTenant(tenantInitService.getTraceTenant(ms.getTenant()));
        generatedDTO.setCreator(mu.getLoginName());
        generatedDTO.setModifier(mu.getLoginName());
        generatedDTO.setGmtCreate(new Date());
        generatedDTO.setGmtModified(new Date());
        generatedDTO.setDeleted(false);
        IntegrationGeneratedDTO insert = integrationGeneratedService.insert(generatedDTO);
        userOpLogService.append("integration_generated", insert.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(insert), null, null,
            "integration_generated_create");
        JsonResult.createSuccessResult(result, insert);
      }
    });

    return result;
  }

  @GetMapping("/query/{name}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<IntegrationAppProductModel>> queryByName(
      @PathVariable("name") String name) {
    final JsonResult<List<IntegrationAppProductModel>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        List<IntegrationAppProductModel> integrationAppProductModels = new ArrayList<>();

        List<IntegrationProductDTO> integrationProductDTOS =
            integrationProductService.queryByRows();
        if (CollectionUtils.isEmpty(integrationProductDTOS)) {
          JsonResult.createSuccessResult(result, integrationAppProductModels);
          return;
        }

        MonitorScope ms = RequestContext.getContext().ms;
        List<IntegrationGeneratedDTO> integrationGeneratedDTOS = integrationGeneratedService
            .queryByName(tenantInitService.getTraceTenant(ms.getTenant()), ms.getWorkspace(), name);

        Map<String, List<IntegrationGeneratedDTO>> generatedMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(integrationGeneratedDTOS)) {
          integrationGeneratedDTOS.forEach(integrationGeneratedDTO -> {
            if (!generatedMap.containsKey(integrationGeneratedDTO.product)) {
              generatedMap.put(integrationGeneratedDTO.product, new ArrayList<>());
            }
            generatedMap.get(integrationGeneratedDTO.product).add(integrationGeneratedDTO);
          });
        }
        Map<String, IntegrationPluginDTO> pluginMap = new HashMap<>();
        List<IntegrationPluginDTO> integrationPluginDTOS =
            integrationPluginService.queryByRows(ms.getTenant(), ms.getWorkspace());
        if (!CollectionUtils.isEmpty(integrationPluginDTOS)) {
          integrationPluginDTOS.forEach(pluginDTO -> {
            pluginMap.put(pluginDTO.product, pluginDTO);
          });
        }

        for (IntegrationProductDTO integrationProductDTO : integrationProductDTOS) {
          if (StringUtils.isBlank(integrationProductDTO.getConfiguration()))
            continue;
          IntegrationConfigDTO configDTO = J.fromJson(integrationProductDTO.getConfiguration(),
              new TypeToken<IntegrationConfigDTO>() {}.getType());
          if (null == configDTO || Boolean.FALSE == configDTO.getUseInApp()) {
            continue;
          }

          Boolean canCustom = configDTO.getCanCustom();

          List<IntegrationAppModel> appModels = new ArrayList<>();
          Set<String> itemMap = new HashSet<>();
          if (generatedMap.containsKey(integrationProductDTO.getName())) {
            generatedMap.get(integrationProductDTO.getName()).forEach(generatedDTO -> {
              itemMap.add(generatedDTO.product + "_" + generatedDTO.item);
              String status;
              if (!CollectionUtils.isEmpty(generatedDTO.config)
                  && generatedDTO.getConfig().containsKey("status")) {
                status = generatedDTO.getConfig().get("status").toString();
              } else {
                status = "OFFLINE";
              }

              appModels.add(new IntegrationAppModel(generatedDTO.id, generatedDTO.item, status,
                  generatedDTO.custom, canCustom, generatedDTO.config));
            });
          }

          if (pluginMap.containsKey(integrationProductDTO.getName())) {
            IntegrationPluginDTO integrationPluginDTO =
                pluginMap.get(integrationProductDTO.getName());
            Plugin plugin = pluginRepository.getTemplate(integrationPluginDTO.type,
                integrationPluginDTO.version);
            if (null == plugin || plugin.getPluginType().equals(PluginType.hosting)) {
              continue;
            }

            AbstractIntegrationPlugin abstractIntegrationPlugin =
                (AbstractIntegrationPlugin) plugin;
            List<AbstractIntegrationPlugin> abstractIntegrationPlugins =
                abstractIntegrationPlugin.genPluginList(integrationPluginDTO);
            if (CollectionUtils.isEmpty(abstractIntegrationPlugins))
              continue;

            if (abstractIntegrationPlugin instanceof LogPlugin) {
              String json = integrationPluginDTO.json;

              Map<String, Object> map = J.toMap(json);
              if (!map.containsKey("confs")) {
                continue;
              }
              List<LogPluginConfig> multiLogPluginConfigs = J.fromJson(J.toJson(map.get("confs")),
                  new TypeToken<List<LogPluginConfig>>() {}.getType());

              multiLogPluginConfigs.forEach(logPluginConfig -> {
                if (itemMap.contains(integrationPluginDTO.product + "_" + logPluginConfig.name)) {
                  return;
                }
                Map<String, Object> configMap = new HashMap<>();
                configMap.put("confs", Collections.singletonList(logPluginConfig));
                appModels.add(new IntegrationAppModel(null, logPluginConfig.name, "OFFLINE", false,
                    canCustom, configMap));
              });
            } else {
              for (AbstractIntegrationPlugin integrationPlugin : abstractIntegrationPlugins) {
                GaeaCollectRange gaeaCollectRange = integrationPlugin.getGaeaCollectRange();

                if (gaeaCollectRange.getType().equalsIgnoreCase("central")
                    || null == gaeaCollectRange.getCloudmonitor()
                    || StringUtils.isBlank(gaeaCollectRange.getCloudmonitor().table)) {
                  continue;
                }

                if (itemMap.contains(integrationPluginDTO.product + "_" + integrationPlugin.name)) {
                  continue;
                }

                appModels.add(new IntegrationAppModel(null, integrationPlugin.name, "OFFLINE",
                    false, canCustom, new HashMap<>()));
              }
            }
          }
          if (CollectionUtils.isEmpty(appModels)) {
            if (Boolean.FALSE == canCustom) {
              continue;
            }
            appModels.add(new IntegrationAppModel(null, integrationProductDTO.name.toLowerCase(),
                "OFFLINE", false, canCustom, new HashMap<>()));
          }
          integrationAppProductModels
              .add(new IntegrationAppProductModel(integrationProductDTO.getName(),
                  integrationProductDTO.profile, name, integrationProductDTO.getForm(), appModels));
        }

        JsonResult.createSuccessResult(result, integrationAppProductModels);
      }
    });

    return result;
  }

  @Data
  @AllArgsConstructor
  public static class IntegrationAppProductModel {

    private String product;

    private String description;

    private String app;

    private IntegrationFormDTO form;

    private List<IntegrationAppModel> appModels;
  }

  @Data
  @AllArgsConstructor
  public static class IntegrationAppModel {

    private Long id;

    private String item;

    private String status;

    private Boolean custom;

    private Boolean canCustom;

    private Map<String, Object> config;
  }

}
