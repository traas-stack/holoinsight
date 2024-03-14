/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.common.MetaDictKey;
import io.holoinsight.server.home.biz.common.MetaDictType;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.biz.service.TraceAgentConfPropService;
import io.holoinsight.server.home.biz.service.TraceAgentConfigurationService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.holoinsight.server.home.dal.model.TraceAgentConfProp;
import io.holoinsight.server.home.dal.model.TraceAgentConfiguration;
import io.holoinsight.server.common.AesUtil;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.config.TraceAuthEncryptConfiguration;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TraceAgentLogTailFacadeImpl.java, v 0.1 2023年08月08日 10:54 上午 jinsong.yjs Exp $
 */

@RequestMapping("/webapi/agent")
public class TraceAgentFacadeImpl extends BaseFacade {
  private final static String TRACE_EXTEND_AUTHENTICATION_PREFIX = "extend";
  private final static String TRACE_AUTHENTICATION = "authentication";

  @Autowired
  private ApiKeyService apiKeyService;

  @Autowired
  private TenantInitService tenantInitService;

  @Autowired
  private TraceAgentConfigurationService agentConfigurationService;
  @Autowired
  private TraceAgentConfPropService traceAgentConfPropService;

  @Autowired
  private TraceAuthEncryptConfiguration traceAuthEncryptConfiguration;

  /**
   * This interface provides some information for the trace access wizard page
   *
   * @param extendInfo
   * @return
   */
  @ResponseBody
  @PostMapping(value = "/traceAgent")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Map<String, Object>> traceAgent(
      @RequestBody(required = false) Map<String, String> extendInfo) {
    String tenant = RequestContext.getContext().ms.getTenant();
    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(), extendInfo);
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        try {
          List<ApiKey> apiKeys = apiKeyService.listByMap(apikeyConditions(tenant, extendInfo));

          Map<String, Object> sysMap = new HashMap<>();
          String apikey = "";
          String collectorHost = getCollectorAddress(extendInfo);

          if (!CollectionUtils.isEmpty(apiKeys)) {
            apikey = apiKeys.get(0).getApiKey();
          }
          if (!CollectionUtils.isEmpty(extendInfo)) {
            extendInfo.put(TRACE_AUTHENTICATION, apikey);
            ObjectMapper objectMapper = new ObjectMapper();
            // extend{"authentication":"xx", "custom_tag1":"xx", "custom_tag2":"xx"}
            // This is an extension scheme to add a custom tag to the span, which must be prefixed
            // with
            // extend, and holoinsight collector will add the custom tag to the span
            apikey =
                TRACE_EXTEND_AUTHENTICATION_PREFIX + objectMapper.writeValueAsString(extendInfo);
            if (traceAuthEncryptConfiguration.isEnable()
                && !StringUtils.isEmpty(traceAuthEncryptConfiguration.getSecretKey())) {
              apikey = AesUtil.aesEncryptHex(apikey, traceAuthEncryptConfiguration.getSecretKey(),
                  traceAuthEncryptConfiguration.getIv());
            }
          }

          getTraceAgentVersion(sysMap);
          sysMap.put("apikey", apikey);
          sysMap.put("collectorHost", collectorHost);
          sysMap.put("traceInstallDocument", MetaDictUtil
              .getStringValue(MetaDictType.TRACE_AGENT_CONFIG, MetaDictKey.TRACE_INSTALL_DOCUMENT));
          JsonResult.createSuccessResult(result, sysMap);
        } catch (Exception e) {
          JsonResult.fillFailResultTo(result, e.getMessage());
        }
      }
    });

    return result;
  }

  @PostMapping(value = "/create/configuration")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> createAgentConfiguration(@RequestBody Map<String, String> request) {
    MonitorUser mu = RequestContext.getContext().mu;
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.get("tenant"), "tenant");
        ParaCheckUtil.checkEquals(request.get("tenant"), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(), request);
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        TraceAgentConfiguration traceAgentConfiguration = new TraceAgentConfiguration();
        traceAgentConfiguration.setTenant(agentConfigurationTenant(request));
        traceAgentConfiguration.setService(request.get("service"));
        traceAgentConfiguration.setType(request.get("type"));
        traceAgentConfiguration.setLanguage(request.get("language"));
        traceAgentConfiguration.setValue(request.get("value"));
        if (mu != null) {
          traceAgentConfiguration.setCreator(mu.getLoginName());
          traceAgentConfiguration.setModifier(mu.getLoginName());
        }
        boolean isSuccess = agentConfigurationService.createOrUpdate(traceAgentConfiguration);
        if (isSuccess) {
          JsonResult.createSuccessResult(result, isSuccess);
        } else {
          JsonResult.fillFailResultTo(result, "Create agent configuration failed!");
        }
      }
    });

    return result;
  }

  @PostMapping(value = "/query/configuration")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<TraceAgentConfiguration> queryAgentConfiguration(
      @RequestBody Map<String, String> request) {
    final JsonResult<TraceAgentConfiguration> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.get("tenant"), "tenant");
        ParaCheckUtil.checkEquals(request.get("tenant"), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
        MonitorScope ms = RequestContext.getContext().ms;
        Boolean aBoolean =
            tenantInitService.checkTraceParams(ms.getTenant(), ms.getWorkspace(), request);
        if (!aBoolean) {
          throw new MonitorException("term params is illegal");
        }
      }

      @Override
      public void doManage() {
        TraceAgentConfiguration traceAgentConfiguration = new TraceAgentConfiguration();
        traceAgentConfiguration.setTenant(agentConfigurationTenant(request));
        traceAgentConfiguration.setService(request.get("service"));
        traceAgentConfiguration.setValue(request.get("value"));
        traceAgentConfiguration.setType(request.get("type"));
        traceAgentConfiguration.setLanguage(request.get("language"));
        TraceAgentConfiguration configuration =
            agentConfigurationService.get(traceAgentConfiguration);
        JsonResult.createSuccessResult(result, configuration);

      }
    });

    return result;
  }

  @GetMapping(value = "/query/configuration/properties")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<TraceAgentConfProp>> queryAgentConfProperties(@RequestParam String type,
      @RequestParam String language) {
    final JsonResult<List<TraceAgentConfProp>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(type, "type");
        ParaCheckUtil.checkParaNotNull(language, "language");
      }

      @Override
      public void doManage() {
        List<TraceAgentConfProp> traceAgentConfProps =
            traceAgentConfPropService.get(type, language);
        JsonResult.createSuccessResult(result, traceAgentConfProps);
      }
    });

    return result;
  }

  public String agentConfigurationTenant(Map<String, String> request) {
    return request.get("tenant");
  }


  public Map<String, Object> apikeyConditions(String tenant, Map<String, String> extendInfo) {
    Map<String, Object> conditions = new HashMap<>();
    conditions.put("tenant", tenantInitService.getTsdbTenant(tenant));
    conditions.put("status", true);
    return conditions;
  }

  public String getCollectorAddress(Map<String, String> extendInfo) {
    return MetaDictUtil.getStringValue(MetaDictType.TRACE_AGENT_CONFIG, MetaDictKey.COLLECTOR_HOST);
  }

  private void getTraceAgentVersion(Map<String, Object> sysMap) {
    String skywalkingJavaAgentVersion = MetaDictUtil.getStringValue(MetaDictType.TRACE_AGENT_CONFIG,
        MetaDictKey.SKYWALKING_JAVA_AGENT_VERSION);
    String skywalkingPythonAgentVersion = MetaDictUtil.getStringValue(
        MetaDictType.TRACE_AGENT_CONFIG, MetaDictKey.SKYWALKING_PYTHON_AGENT_VERSION);
    String skywalkingPHPAgentVersion = MetaDictUtil.getStringValue(MetaDictType.TRACE_AGENT_CONFIG,
        MetaDictKey.SKYWALKING_PHP_AGENT_VERSION);
    String skywalkingDotnetAgentVersion = MetaDictUtil.getStringValue(
        MetaDictType.TRACE_AGENT_CONFIG, MetaDictKey.SKYWALKING_DOTNET_AGENT_VERSION);
    String skywalkingNodejsAgentVersion = MetaDictUtil.getStringValue(
        MetaDictType.TRACE_AGENT_CONFIG, MetaDictKey.SKYWALKING_NODEJS_AGENT_VERSION);
    String skywalkingGOAgentVersion = MetaDictUtil.getStringValue(MetaDictType.TRACE_AGENT_CONFIG,
        MetaDictKey.SKYWALKING_GO_AGENT_VERSION);

    if (StringUtils.isEmpty(skywalkingJavaAgentVersion)) {
      skywalkingJavaAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_JAVA_AGENT_VERSION;
    }
    if (StringUtils.isEmpty(skywalkingPythonAgentVersion)) {
      skywalkingPythonAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_PYTHON_AGENT_VERSION;
    }
    if (StringUtils.isEmpty(skywalkingPHPAgentVersion)) {
      skywalkingPHPAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_PHP_AGENT_VERSION;
    }
    if (StringUtils.isEmpty(skywalkingDotnetAgentVersion)) {
      skywalkingDotnetAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_DOTNET_AGENT_VERSION;
    }
    if (StringUtils.isEmpty(skywalkingNodejsAgentVersion)) {
      skywalkingNodejsAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_NODEJS_AGENT_VERSION;
    }
    if (StringUtils.isEmpty(skywalkingGOAgentVersion)) {
      skywalkingGOAgentVersion = MetaDictKey.DEFAULT_SKYWALKING_GO_AGENT_VERSION;
    }

    sysMap.put(MetaDictKey.SKYWALKING_JAVA_AGENT_VERSION, skywalkingJavaAgentVersion);
    sysMap.put(MetaDictKey.SKYWALKING_PYTHON_AGENT_VERSION, skywalkingPythonAgentVersion);
    sysMap.put(MetaDictKey.SKYWALKING_PHP_AGENT_VERSION, skywalkingPHPAgentVersion);
    sysMap.put(MetaDictKey.SKYWALKING_DOTNET_AGENT_VERSION, skywalkingDotnetAgentVersion);
    sysMap.put(MetaDictKey.SKYWALKING_NODEJS_AGENT_VERSION, skywalkingNodejsAgentVersion);
    sysMap.put(MetaDictKey.SKYWALKING_GO_AGENT_VERSION, skywalkingGOAgentVersion);
  }

}
