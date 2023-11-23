/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.DisplayMenuService;
import io.holoinsight.server.home.biz.service.IntegrationGeneratedService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuConfig;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuFacadeImpl.java, v 0.1 2022年12月06日 上午10:34 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/displaymenu")
public class DisplayMenuFacadeImpl extends BaseFacade {

  @Autowired
  private DisplayMenuService displayMenuService;

  @Autowired
  private IntegrationGeneratedService integrationGeneratedService;


  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<DisplayMenuDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<DisplayMenuDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        DisplayMenuDTO menu =
            displayMenuService.queryById(id, RequestContext.getContext().ms.getTenant());

        if (null == menu) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, menu);
      }
    });
    return result;
  }

  @GetMapping(value = "/query/{type}/{refId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<DisplayMenuDTO>> queryByRefId(@PathVariable("type") String type,
      @PathVariable("refId") Long refId) {
    final JsonResult<List<DisplayMenuDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(type, "type");
        ParaCheckUtil.checkParaNotNull(refId, "refId");
      }

      @Override
      public void doManage() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("type", type);
        columnMap.put("ref_id", refId);

        List<DisplayMenuDTO> byMap = displayMenuService.findByMap(columnMap);

        if (null == byMap) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, byMap);
      }
    });
    return result;
  }

  @GetMapping(value = "/query/apm/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<DisplayMenuConfig>> queryByRefId(@PathVariable("name") String name) {
    final JsonResult<List<DisplayMenuConfig>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> apmMenuMap = new HashMap<>();
        DisplayMenuDTO displayMenuDTO;
        apmMenuMap.put("type", "apm");
        apmMenuMap.put("ref_id", -1);
        List<DisplayMenuDTO> byMap = displayMenuService.findByMap(apmMenuMap);

        if (CollectionUtils.isEmpty(byMap)) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        displayMenuDTO = byMap.get(0);

        Map<String, Object> apmMenuTenantMap = new HashMap<>();
        apmMenuMap.put("type", "apm");
        apmMenuMap.put("ref_id", -1);
        apmMenuMap.put("tenant", ms.getTenant());
        List<DisplayMenuDTO> byTenantMap = displayMenuService.findByMap(apmMenuTenantMap);
        if (!CollectionUtils.isEmpty(byTenantMap)) {
          displayMenuDTO = byTenantMap.get(0);
        }

        Boolean defaultApmDisplayMenu = MetaDictUtil.isDefaultApmDisplayMenu();
        if (defaultApmDisplayMenu) {
          JsonResult.createSuccessResult(result, displayMenuDTO.getConfig());
          return;
        }
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("tenant", ms.getTenant());
        columnMap.put("name", name);
        columnMap.put("deleted", 0);

        List<IntegrationGenerated> generateds = integrationGeneratedService.listByMap(columnMap);

        if (CollectionUtils.isEmpty(generateds)) {
          JsonResult.createSuccessResult(result, displayMenuDTO.getConfig());
          return;
        }

        Set<String> itemSets = new HashSet<>();

        generateds.forEach(generated -> {
          itemSets.add(generated.item);
        });

        try {
          List<DisplayMenuConfig> menuConfigs =
              convertApmMenu(displayMenuDTO.getConfig(), itemSets);
          JsonResult.createSuccessResult(result, menuConfigs);
        } catch (Exception e) {
          log.error("convertApmMenu error", e);
          JsonResult.createSuccessResult(result, displayMenuDTO.getConfig());
        }

      }
    });
    return result;
  }

  private List<DisplayMenuConfig> convertApmMenu(List<DisplayMenuConfig> configs,
      Set<String> itemSets) {

    List<DisplayMenuConfig> menuConfigs = new ArrayList<>();

    for (DisplayMenuConfig config : configs) {
      if (StringUtils.isBlank(config.getShowItem())
          && CollectionUtils.isEmpty(config.getChildren())) {
        menuConfigs.add(config);
      } else if (itemSets.contains(config.getShowItem())) {
        menuConfigs.add(config);
      }

      if (!CollectionUtils.isEmpty(config.getChildren())) {

        DisplayMenuConfig update = new DisplayMenuConfig();
        BeanUtils.copyProperties(config, update);
        update.setChildren(new ArrayList<>());

        for (DisplayMenuConfig child : config.getChildren()) {
          if (StringUtils.isBlank(child.getShowItem())
              && CollectionUtils.isEmpty(child.getChildren())) {
            update.getChildren().add(child);
          } else if (itemSets.contains(child.getShowItem())) {
            update.getChildren().add(child);
          }
        }
        if (!CollectionUtils.isEmpty(update.getChildren())) {
          menuConfigs.add(update);
        }
      }
    }

    return menuConfigs;
  }
}
