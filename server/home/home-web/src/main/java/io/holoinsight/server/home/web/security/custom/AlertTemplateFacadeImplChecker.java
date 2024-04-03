/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.mapper.AlertTemplateMapper;
import io.holoinsight.server.common.dao.entity.AlertTemplate;
import io.holoinsight.server.common.dao.entity.dto.AlertTemplateDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-01-26 10:39:00
 */
@Slf4j
@Service
public class AlertTemplateFacadeImplChecker implements LevelAuthorizationCheck {

  @Resource
  private AlertTemplateMapper alertTemplateMapper;

  private static final Set<String> sceneTypeSet =
      new HashSet<>(Arrays.asList("miniapp", "server", "iot"));
  private static final Set<String> channelTypeSet = new HashSet<>(Arrays.asList("sms", "dingTalk"));

  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    MonitorScope ms = RequestContext.getContext().ms;
    String workspace = ms.getWorkspace();
    String tenant = ms.getTenant();

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace) {
    switch (methodName) {
      case "create":
      case "update":
      case "checkTemplateText":
        return checkAlertNotificationTemplateDTO(methodName, parameters, tenant, workspace);
      case "queryById":
      case "deleteById":
        return checkId(parameters, tenant, workspace);
      case "pageQuery":
        return checkPageRequest(methodName, parameters, tenant, workspace);
      default:
        return successCheckResult();
    }
  }

  private LevelAuthorizationCheckResult checkPageRequest(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlertTemplateDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlertTemplateDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %s larger than end %s",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    AlertTemplateDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkAlertNotificationTemplateDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkId(List<String> parameters, String tenant,
      String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }

    String uuid = J.fromJson(parameters.get(0), String.class);
    return checkId(uuid, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkId(String uuid, String tenant, String workspace) {
    QueryWrapper<AlertTemplate> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("uuid", uuid);
    queryWrapper.eq("tenant", tenant);
    queryWrapper.eq("workspace", workspace);
    List<AlertTemplate> exist = this.alertTemplateMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      return failCheckResult("fail to check uuid for no existed %s %s %s", uuid, tenant, workspace);
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkAlertNotificationTemplateDTO(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlertTemplateDTO templateDTO = J.fromJson(parameters.get(0), AlertTemplateDTO.class);
    return checkAlertNotificationTemplateDTO(methodName, templateDTO, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlertNotificationTemplateDTO(String methodName,
      AlertTemplateDTO templateDTO, String tenant, String workspace) {

    if (methodName.equals("create")) {
      if (StringUtils.isNotEmpty(templateDTO.uuid)) {
        return failCheckResult("fail to check %s for uuid is not null", methodName);
      }
    }

    if (methodName.equals("update")) {
      if (StringUtils.isEmpty(templateDTO.uuid)) {
        return failCheckResult("fail to check %s for uuid is null", methodName);
      }
      LevelAuthorizationCheckResult checkResult = checkId(templateDTO.uuid, tenant, workspace);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }

    if (StringUtils.isNotEmpty(templateDTO.templateName)
        && !checkSqlName(templateDTO.templateName)) {
      return failCheckResult("fail to check %s for invalid templateName %s", methodName,
          templateDTO.templateName);
    }

    if (StringUtils.isNotEmpty(templateDTO.sceneType)
        && !sceneTypeSet.contains(templateDTO.sceneType)) {
      return failCheckResult("fail to check %s for invalid sceneType %s", methodName,
          templateDTO.sceneType);
    }

    if (StringUtils.isNotEmpty(templateDTO.channelType)
        && !channelTypeSet.contains(templateDTO.channelType)) {
      return failCheckResult("fail to check %s for invalid channelType %s", methodName,
          templateDTO.channelType);
    }

    if (StringUtils.isNotEmpty(templateDTO.tenant)
        && !StringUtils.equals(templateDTO.tenant, tenant)) {
      return failCheckResult("fail to check %s for invalid tenant %s", methodName,
          templateDTO.tenant);
    }

    if (StringUtils.isNotEmpty(templateDTO.workspace)
        && !StringUtils.equals(templateDTO.workspace, workspace)) {
      return failCheckResult("fail to check %s for invalid workspace %s", methodName,
          templateDTO.workspace);
    }

    MonitorUser mu = RequestContext.getContext().mu;
    if (StringUtils.isNotEmpty(templateDTO.creator)
        && !StringUtils.equals(templateDTO.creator, mu.getLoginName())) {
      return failCheckResult("fail to check %s for invalid creator %s for login name %s",
          methodName, templateDTO.creator, mu.getLoginName());
    }

    if (StringUtils.isNotEmpty(templateDTO.modifier)
        && !StringUtils.equals(templateDTO.modifier, mu.getLoginName())) {
      return failCheckResult("fail to check %s for invalid modifier %s for login name %s",
          methodName, templateDTO.modifier, mu.getLoginName());
    }

    if (StringUtils.isNotEmpty(templateDTO.description) && !checkSqlName(templateDTO.description)) {
      return failCheckResult("fail to check %s for invalid description %s", methodName,
          templateDTO.description);
    }
    return successCheckResult();
  }
}
