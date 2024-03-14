/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsDTO;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage.StorageMetric;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.biz.service.TenantOpsService;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO.TableStatus;
import io.holoinsight.server.home.dal.model.dto.meta.MetaTableCol;
import io.holoinsight.server.home.dal.model.dto.meta.MetaTableConfig;
import io.holoinsight.server.home.common.util.ManageCallback;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: InitFacadeImpl.java, v 0.1 2022年06月21日 2:32 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/init")
@Slf4j
public class InitFacadeImpl extends BaseFacade {

  private static String DEFAULT_STORAGE_TENANT = "default";

  @Autowired
  private MetaTableService metaTableService;


  @Autowired
  private ApiKeyService apiKeyService;

  @Autowired
  private TenantOpsService tenantOpsService;


  @Autowired
  private TenantInitService tenantInitService;

  @ResponseBody
  @GetMapping(value = "/tenantCheck")
  public JsonResult<Boolean> tenantCheck() {

    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        String tenant = RequestContext.getContext().ms.getTenant();
        Map<String, Object> colMap = new HashMap<>();
        colMap.put("tenant", tenant);
        Debugger.print("InitFacadeImpl", "currentTenant: " + tenant);
        List<TenantOps> tenantOps = tenantOpsService.listByMap(colMap);
        if (CollectionUtils.isEmpty(tenantOps)) {
          JsonResult.createSuccessResult(result, true);
          return;
        }

        List<MetaTableDTO> byTenant = metaTableService.findByTenant(tenant);
        if (CollectionUtils.isEmpty(byTenant)) {
          JsonResult.createSuccessResult(result, true);
          return;
        }

        JsonResult.createSuccessResult(result, false);
      }
    });
    return result;

  }

  @ResponseBody
  @GetMapping(value = "/tenantSwitch/{tenant}")
  public JsonResult<Boolean> tenantSwitch(@PathVariable("tenant") String tenant,
      @RequestParam(value = "workspace", required = false) String workspace,
      @RequestParam(value = "environment", required = false) String environment,
      HttpServletResponse response) {

    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tenant, "tenant");
        tenantInitService.checkCookie(tenant, workspace, environment);
      }

      @Override
      public void doManage() {
        MonitorCookieUtil.addTenantCookie(tenant, response);
        MonitorCookieUtil.addTenantWorkspaceCookie(workspace, response);
        MonitorCookieUtil.addTenantEnvironmentCookie(environment, response);
        JsonResult.createSuccessResult(result, true);
      }
    });
    return result;

  }

  @ResponseBody
  @GetMapping(value = "/tenant")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> sys() {

    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        // step1 存储空间配置 保存到 tenant_ops 表中
        TenantOpsDTO tenantOpsTable = createTenantOpsTable(DEFAULT_STORAGE_TENANT);
        log.info("init tenant, create tenant ops: " + J.toJson(tenantOpsTable));

        // step2 元数据默认创建schema
        MetaTableDTO table = createTable();
        log.info("init tenant, create table: " + J.toJson(table));

        // step3 新创建一个租户对应的API KEY
        ApiKey apiKey = createApiKey();
        log.info("init apikey : " + J.toJson(apiKey));

        // step
        // 创建元数据索引

        // step4
        JsonResult.createSuccessResult(result, true);
      }
    });
    return result;

  }

  private ApiKey createApiKey() {

    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;
    Map<String, Object> conditions = new HashMap<>();
    conditions.put("tenant", ms.getTenant());
    conditions.put("status", true);
    List<ApiKey> apiKeys = apiKeyService.listByMap(conditions);

    if (CollectionUtils.isEmpty(apiKeys)) {
      ApiKey apiKey = new ApiKey();
      if (null != mu) {
        apiKey.setCreator(mu.getLoginName());
        apiKey.setModifier(mu.getLoginName());
      }
      if (!StringUtils.isEmpty(ms.tenant)) {
        apiKey.setTenant(ms.tenant);
      }
      apiKey.setStatus(true);
      apiKey.setName("init");
      apiKey.setDesc("初始化创建");
      apiKey.setApiKey(UUID.randomUUID().toString());
      apiKey.setGmtCreate(new Date());
      apiKey.setGmtModified(new Date());
      apiKeyService.save(apiKey);
      return apiKey;
    }

    return apiKeys.get(0);
  }

  private TenantOpsDTO createTenantOpsTable(String storageTenant) {
    String originalTenant = RequestContext.getContext().ms.getTenant();
    StorageMetric storageMetric = tenantInitService.createStorageMetric(storageTenant);

    TenantOpsDTO tenantOpsDTO = new TenantOpsDTO();
    tenantOpsDTO.setTenant(originalTenant);

    TenantOpsStorage tenantOpsStorage = new TenantOpsStorage();
    tenantOpsStorage.setMetric(storageMetric);

    tenantOpsDTO.setStorage(tenantOpsStorage);

    tenantOpsService.createOrUpdate(tenantOpsDTO);

    return tenantOpsDTO;
  }

  private MetaTableDTO createTable() {
    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;
    MetaTableDTO metaTable = new MetaTableDTO();
    metaTable.setCreator(mu.getLoginName());
    metaTable.setModifier(mu.getLoginName());
    metaTable.setTenant(ms.getTenant());
    metaTable.setGmtCreate(new Date());
    metaTable.setGmtModified(new Date());

    metaTable.setName(String.format("%s_server", ms.getTenant()));
    metaTable.setStatus(TableStatus.ONLINE);

    List<MetaTableCol> tableSchema = new ArrayList<>();

    metaTable.setTableSchema(tableSchema);

    MetaTableConfig metaTableConfig = new MetaTableConfig();
    metaTableConfig.setSource("input");
    metaTableConfig.setRateSec(60);
    metaTableConfig.setMetricList(new ArrayList<>());

    Map<String, List<String>> ukMaps = new HashMap<>();
    ukMaps.put("vm", Collections.singletonList("ip"));
    ukMaps.put("node", Collections.singletonList("name"));
    ukMaps.put("pod", Arrays.asList("name", "namespace"));
    ukMaps.put("container", Arrays.asList("name", "namespace"));
    metaTableConfig.setUkMaps(ukMaps);

    metaTable.setConfig(metaTableConfig);

    return metaTableService.insertOrUpate(metaTable);
  }
}
