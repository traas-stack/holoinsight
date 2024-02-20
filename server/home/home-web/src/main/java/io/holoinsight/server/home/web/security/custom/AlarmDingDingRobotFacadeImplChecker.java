/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.AlarmDingDingRobotMapper;
import io.holoinsight.server.home.dal.model.AlarmDingDingRobot;
import io.holoinsight.server.home.dal.model.dto.AlarmDingDingRobotDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.web.controller.AlarmDingDingRobotFacadeImpl.dingdingUrlPrefix;

/**
 * @author masaimu
 * @version 2024-01-29 11:32:00
 */
@Slf4j
@Service
public class AlarmDingDingRobotFacadeImplChecker extends AbstractResourceChecker {

  @Autowired
  private AlarmDingDingRobotMapper alarmDingDingRobotMapper;
  @Autowired
  private ParameterSecurityService parameterSecurityService;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public boolean check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    MonitorScope ms = RequestContext.getContext().ms;
    String workspace = this.requestContextAdapter.getWorkspace(true);
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
        return checkAlarmDingDingRobotDTO(methodName, parameters, tenant, workspace);
      case "queryById":
        return checkIdNotNull(parameters);
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
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
    MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest = J.fromJson(parameter,
        new TypeToken<MonitorPageRequest<AlarmDingDingRobotDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        log.error("fail to check time range for start {} larger than end {}", pageRequest.getFrom(),
            pageRequest.getTo());
        return false;
      }
    }

    AlarmDingDingRobotDTO target = pageRequest.getTarget();
    if (target == null) {
      log.error("fail to check target, target can not be null");
      return false;
    }
    return checkAlarmDingDingRobotDTO(methodName, target, tenant, workspace);
  }



  private boolean checkAlarmDingDingRobotDTO(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return false;
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlarmDingDingRobotDTO dto = J.fromJson(parameters.get(0), AlarmDingDingRobotDTO.class);
    return checkAlarmDingDingRobotDTO(methodName, dto, tenant, workspace);
  }

  private boolean checkAlarmDingDingRobotDTO(String methodName, AlarmDingDingRobotDTO dto,
      String tenant, String workspace) {
    if (methodName.equals("create")) {
      if (dto.getId() != null) {
        log.error("fail to check {} for id is not null", methodName);
        return false;
      }
      if (StringUtils.isBlank(dto.getGroupName())) {
        log.error("group name can not be blank.");
        return false;
      }
    }

    if (methodName.equals("update")) {
      if (dto.getId() == null) {
        log.error("fail to check {} for id is null", methodName);
        return false;
      }
      if (!checkIdExists(dto.getId(), tenant, workspace)) {
        return false;
      }
    }

    if (StringUtils.isNotEmpty(dto.getRobotUrl())
        && !dto.getRobotUrl().startsWith(dingdingUrlPrefix)) {
      log.error("invalid robotUrl {}", dto.getRobotUrl());
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getCreator()) && !checkSqlField(dto.getCreator())) {
      log.error("fail to check {} for invalid creator {}", methodName, dto.getCreator());
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getModifier()) && !checkSqlField(dto.getModifier())) {
      log.error("fail to check {} for invalid modifier {}", methodName, dto.getModifier());
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getGroupName()) && !checkSqlField(dto.getGroupName())) {
      log.error("fail to check {} for invalid group name {}", methodName, dto.getGroupName());
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getTenant()) && !StringUtils.equals(dto.getTenant(), tenant)) {
      log.error("fail to check {} for invalid tenant {} for valid tenant {}", methodName,
          dto.getTenant(), tenant);
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getWorkspace())
        && !StringUtils.equals(dto.getWorkspace(), workspace)) {
      log.error("fail to check {} for invalid workspace {} for valid workspace {}", methodName,
          dto.getWorkspace(), workspace);
      return false;
    }

    if (StringUtils.isNotEmpty(dto.getExtra()) && !checkUserIds(dto.getExtra())) {
      log.error("fail to check {} that userIds in extra {}", methodName, dto.getExtra());
      return false;
    }

    return true;
  }

  private boolean checkUserIds(String extra) {
    Map<String, Object> extraMap = J.toMap(extra);
    List<String> userIds = (List<String>) extraMap.get("userIds");
    if (CollectionUtils.isEmpty(userIds)) {
      return true;
    }
    MonitorUser mu = RequestContext.getContext().mu;
    for (String uid : userIds) {
      if (!this.parameterSecurityService.checkUserTenantAndWorkspace(uid, mu)) {
        log.error("fail to check uid {}", uid);
        return false;
      }
    }
    return true;
  }

  @Override
  boolean checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmDingDingRobot> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);

    List<AlarmDingDingRobot> exist = this.alarmDingDingRobotMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      log.error("fail to check id for no existed {} {} {}", id, tenant, workspace);
      return false;
    }
    return true;
  }
}
