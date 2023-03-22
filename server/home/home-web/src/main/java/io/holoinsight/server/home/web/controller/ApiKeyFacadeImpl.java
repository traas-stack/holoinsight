/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.ApiKeyService;
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
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author jsy1001de
 * @version 1.0: ApiKeyFacadeImpl.java, v 0.1 2022年05月31日 11:29 上午 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/apikey")
public class ApiKeyFacadeImpl extends BaseFacade {
  @Autowired
  private ApiKeyService apiKeyService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody ApiKey apiKey) {
    final JsonResult<ApiKey> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(apiKey.id, "id");
        ParaCheckUtil.checkParaNotBlank(apiKey.apiKey, "apiKey");
        ParaCheckUtil.checkParaNotBlank(apiKey.name, "name");
        ParaCheckUtil.checkParaNotNull(apiKey.status, "status");

        ParaCheckUtil.checkParaNotNull(apiKey.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(apiKey.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");

        ApiKey item =
            apiKeyService.queryById(apiKey.getId(), RequestContext.getContext().ms.getTenant());

        if (null == item) {
          throw new MonitorException("cannot find record: " + apiKey.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(apiKey.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        ApiKey update = new ApiKey();

        BeanUtils.copyProperties(apiKey, update);

        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          update.setTenant(ms.tenant);
        }
        update.setGmtModified(new Date());
        apiKeyService.updateById(update);

        assert mu != null;
        userOpLogService.append("apikey", apiKey.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(apiKey), J.toJson(update), null,
            "apikey_update");
      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<ApiKey> save(@RequestBody ApiKey apiKey) {
    final JsonResult<ApiKey> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(apiKey.name, "name");
        ParaCheckUtil.checkParaNotNull(apiKey.status, "status");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          apiKey.setCreator(mu.getLoginName());
          apiKey.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          apiKey.setTenant(ms.tenant);
        }
        apiKey.setApiKey(UUID.randomUUID().toString());
        apiKey.setTenant(MonitorCookieUtil.getTenantOrException());
        apiKey.setGmtCreate(new Date());
        apiKey.setGmtModified(new Date());
        apiKeyService.save(apiKey);
        JsonResult.createSuccessResult(result, apiKey);

        assert mu != null;
        userOpLogService.append("apikey", apiKey.getId(), OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(apiKey), null, null, "apikey_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<ApiKey> queryById(@PathVariable("id") Long id) {
    final JsonResult<ApiKey> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        ApiKey apiKey = apiKeyService.queryById(id, RequestContext.getContext().ms.getTenant());

        if (null == apiKey) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, apiKey);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<ApiKey>> queryAll() {
    final JsonResult<List<ApiKey>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("tenant", ms.getTenant());

        List<ApiKey> apiKeys = apiKeyService.listByMap(conditions);
        apiKeys.forEach(apiKey -> {
          apiKey.setCreator(null);
          apiKey.setModifier(null);
        });
        JsonResult.createSuccessResult(result, apiKeys);
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
        ApiKey byId = apiKeyService.queryById(id, ms.getTenant());
        if (byId == null) {
          return;
        }

        apiKeyService.removeById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("apikey", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "apikey_delete");

      }
    });
    return result;
  }

  @DeleteMapping(value = "/deleteByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> deleteByName(@PathVariable("name") String name) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", name);
        columnMap.put("tenant", ms.getTenant());
        List<ApiKey> apiKeys = apiKeyService.listByMap(columnMap);
        if (CollectionUtils.isEmpty(apiKeys)) {
          return;
        }

        List<Long> ids = apiKeys.stream().map(ApiKey::getId).collect(Collectors.toList());

        apiKeyService.removeBatchByIds(ids);
        JsonResult.createSuccessResult(result, null);
        if (!CollectionUtils.isEmpty(ids)) {
          userOpLogService.append("apikey", ids.get(0), OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
              J.toJson(apiKeys), null, null, "apikey_delete");
        }
      }
    });
    return result;
  }
}
