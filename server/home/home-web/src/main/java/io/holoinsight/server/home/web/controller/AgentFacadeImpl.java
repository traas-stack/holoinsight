/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.common.MetaDictKey;
import io.holoinsight.server.home.biz.common.MetaDictType;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.biz.service.agent.AgentLogTailService;
import io.holoinsight.server.home.biz.service.agent.AgentParamRequest;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentLogTailFacadeImpl.java, v 0.1 2022年04月24日 10:54 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/agent")
public class AgentFacadeImpl extends BaseFacade {

  @Autowired
  private AgentLogTailService agentLogTailService;

  @Autowired
  private ApiKeyService apiKeyService;

  @ResponseBody
  @GetMapping(value = "/vmAgent")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> vmAgent() {
    String tenant = RequestContext.getContext().ms.getTenant();
    Map<String, Object> conditions = new HashMap<>();
    conditions.put("tenant", tenant);
    conditions.put("status", true);
    conditions.put("name", "init");
    List<ApiKey> apiKeys = apiKeyService.listByMap(conditions);

    Map<String, Object> sysMap = new HashMap<>();

    sysMap.put("vmInstallDocument", MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG,
        MetaDictKey.VM_AGENT_INSTALL_DOCUMENT));

    sysMap.put("k8sInstallDocument", MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG,
        MetaDictKey.K8S_AGENT_INSTALL_DOCUMENT));

    if (CollectionUtils.isEmpty(apiKeys)) {
      sysMap.put("apiKey", "");
    } else {
      sysMap.put("apiKey", apiKeys.get(0).getApiKey());
    }
    sysMap.put("installHost",
        MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG, MetaDictKey.AGENT_INSTALL_HOST));
    sysMap.put("registryHost",
        MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG, MetaDictKey.AGENT_REGISTRY_HOST));
    sysMap.put("gatewayHost",
        MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG, MetaDictKey.AGENT_GATEWAY_HOST));
    sysMap.put("installPackage", MetaDictUtil.getStringValue(MetaDictType.VM_AGENT_CONFIG,
        MetaDictKey.AGENT_INSTALL_PACKAGE));
    return JsonResult.createSuccessResult(sysMap);
  }

  @PostMapping(value = "/listFiles")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> listFiles(
      @RequestBody AgentParamRequest agentParamRequest) {

    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(agentParamRequest.getLogpath(), "logpath");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        try {
          JsonResult.createSuccessResult(result, agentLogTailService
              .listFiles(agentParamRequest, ms.getTenant(), ms.getWorkspace()).getDatas());
        } catch (MonitorException e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        } catch (Exception e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        }
      }
    });

    return result;
  }

  @PostMapping(value = "/previewFile")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> previewFile(
      @RequestBody AgentParamRequest agentParamRequest) {
    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(agentParamRequest.getLogpath(), "logpath");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        try {
          JsonResult.createSuccessResult(result, agentLogTailService
              .previewFile(agentParamRequest, ms.getTenant(), ms.getWorkspace()).getDatas());
        } catch (MonitorException e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        } catch (Exception e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        }
      }
    });

    return result;
  }

  @PostMapping(value = "/inspect")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> inspect(@RequestBody AgentParamRequest agentParamRequest) {
    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(agentParamRequest.getHostname(), "ip");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        try {
          JsonResult.createSuccessResult(result, agentLogTailService
              .inspect(agentParamRequest, ms.getTenant(), ms.getWorkspace()).getDatas());

        } catch (MonitorException e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        } catch (Exception e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        }
      }
    });

    return result;
  }
}
