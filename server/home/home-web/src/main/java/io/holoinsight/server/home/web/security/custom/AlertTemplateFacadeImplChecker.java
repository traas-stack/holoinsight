/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.AlertTemplateMapper;
import io.holoinsight.server.home.dal.model.AlertTemplate;
import io.holoinsight.server.home.facade.AlertTemplateDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
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
  public boolean check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    MonitorScope ms = RequestContext.getContext().ms;
    String workspace = ms.getWorkspace();
    String tenant = ms.getTenant();

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private boolean checkParameters(String methodName, List<String> parameters, String tenant,
      String workspace) {
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
        return true;
    }
  }

  private boolean checkPageRequest(String methodName, List<String> parameters, String tenant,
      String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return false;
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlertTemplateDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlertTemplateDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        log.error("fail to check time range for start {} larger than end {}", pageRequest.getFrom(),
            pageRequest.getTo());
        return false;
      }
    }

    AlertTemplateDTO target = pageRequest.getTarget();
    if (target == null) {
      log.error("fail to check target, target can not be null");
      return false;
    }
    return checkAlertNotificationTemplateDTO(methodName, target, tenant, workspace);
  }

  private boolean checkId(List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return false;
    }

    String uuid = J.fromJson(parameters.get(0), String.class);
    return checkId(uuid, tenant, workspace);
  }

  private boolean checkId(String uuid, String tenant, String workspace) {
    QueryWrapper<AlertTemplate> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("uuid", uuid);
    queryWrapper.eq("tenant", tenant);
    queryWrapper.eq("workspace", workspace);
    List<AlertTemplate> exist = this.alertTemplateMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      log.error("fail to check uuid for no existed {} {} {}", uuid, tenant, workspace);
      return false;
    }
    return true;
  }

  private boolean checkAlertNotificationTemplateDTO(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return false;
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlertTemplateDTO templateDTO = J.fromJson(parameters.get(0), AlertTemplateDTO.class);
    return checkAlertNotificationTemplateDTO(methodName, templateDTO, tenant, workspace);
  }

  private boolean checkAlertNotificationTemplateDTO(String methodName, AlertTemplateDTO templateDTO,
      String tenant, String workspace) {

    if (methodName.equals("create")) {
      if (StringUtils.isNotEmpty(templateDTO.uuid)) {
        log.error("fail to check {} for uuid is not null", methodName);
        return false;
      }
    }

    if (methodName.equals("update")) {
      if (StringUtils.isEmpty(templateDTO.uuid)) {
        log.error("fail to check {} for uuid is null", methodName);
        return false;
      }
      if (!checkId(templateDTO.uuid, tenant, workspace)) {
        return false;
      }
    }

    if (StringUtils.isNotEmpty(templateDTO.templateName)
        && !checkSqlName(templateDTO.templateName)) {
      log.error("fail to check {} for invalid templateName {}", methodName,
          templateDTO.templateName);
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.sceneType)
        && !sceneTypeSet.contains(templateDTO.sceneType)) {
      log.error("fail to check {} for invalid sceneType {}", methodName, templateDTO.sceneType);
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.channelType)
        && !channelTypeSet.contains(templateDTO.channelType)) {
      log.error("fail to check {} for invalid channelType {}", methodName, templateDTO.channelType);
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.tenant)
        && !StringUtils.equals(templateDTO.tenant, tenant)) {
      log.error("fail to check {} for invalid tenant {}", methodName, templateDTO.tenant);
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.workspace)
        && !StringUtils.equals(templateDTO.workspace, workspace)) {
      log.error("fail to check {} for invalid workspace {}", methodName, templateDTO.workspace);
      return false;
    }

    MonitorUser mu = RequestContext.getContext().mu;
    if (StringUtils.isNotEmpty(templateDTO.creator)
        && !StringUtils.equals(templateDTO.creator, mu.getLoginName())) {
      log.error("fail to check {} for invalid creator {} for login name {}", methodName,
          templateDTO.creator, mu.getLoginName());
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.modifier)
        && !StringUtils.equals(templateDTO.modifier, mu.getLoginName())) {
      log.error("fail to check {} for invalid modifier {} for login name {}", methodName,
          templateDTO.modifier, mu.getLoginName());
      return false;
    }

    if (StringUtils.isNotEmpty(templateDTO.description) && !checkSqlName(templateDTO.description)) {
      log.error("fail to check {} for invalid description {}", methodName, templateDTO.description);
      return false;
    }
    return true;
  }
}
