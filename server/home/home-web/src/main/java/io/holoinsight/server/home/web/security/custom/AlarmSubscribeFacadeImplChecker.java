/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.entity.AlarmDingDingRobot;
import io.holoinsight.server.common.dao.entity.AlarmGroup;
import io.holoinsight.server.common.dao.entity.AlarmRule;
import io.holoinsight.server.common.dao.entity.AlarmSubscribe;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeDTO;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeInfo;
import io.holoinsight.server.common.dao.mapper.AlarmDingDingRobotMapper;
import io.holoinsight.server.common.dao.mapper.AlarmGroupMapper;
import io.holoinsight.server.common.dao.mapper.AlarmRuleMapper;
import io.holoinsight.server.common.dao.mapper.AlarmSubscribeMapper;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @version 2024-05-27 16:58:00
 */
@Slf4j
@Service
public class AlarmSubscribeFacadeImplChecker
    implements AbstractResourceChecker, LevelAuthorizationCheck {

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  @Autowired
  private ParameterSecurityService parameterSecurityService;
  @Resource
  private AlarmSubscribeMapper alarmSubscribeMapper;
  @Resource
  private AlarmGroupMapper alarmGroupMapper;
  @Resource
  private AlarmRuleMapper alarmRuleMapper;
  @Resource
  private AlarmDingDingRobotMapper dingDingRobotMapper;

  private static final Set<String> Notice_Type_Set =
      new HashSet<>(Arrays.asList("dingding", "sms", "phone", "email", "dingDingRobot"));


  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    String workspace =
        this.requestContextAdapter.getWorkspaceFromContext(RequestContext.getContext());
    String groupWorkspace =
        this.requestContextAdapter.getSimpleWorkspaceFromContext(RequestContext.getContext());
    String tenant = this.requestContextAdapter.getTenantFromContext(RequestContext.getContext());

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace, groupWorkspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace, String groupWorkspace) {
    switch (methodName) {
      case "submit":
        return checkAlarmSubscribeDTO(methodName, parameters, tenant, workspace, groupWorkspace);
      case "querySubUsers":
      case "queryByUniqueId":
        return checkQueryByUniqueId(methodName, parameters, tenant, workspace);
      default:
        return successCheckResult();
    }
  }

  private LevelAuthorizationCheckResult checkQueryByUniqueId(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    String ruleId = J.fromJson(parameters.get(0), String.class);
    if (!checkUniqueId(ruleId, tenant, workspace)) {
      return failCheckResult("ruleId %s no exists for tenant %s workspace %s.", ruleId, tenant,
          workspace);
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkAlarmSubscribeDTO(String methodName,
      List<String> parameters, String tenant, String workspace, String groupWorkspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlarmSubscribeDTO alarmSubscribeDTO = J.fromJson(parameters.get(0), AlarmSubscribeDTO.class);
    return checkAlarmSubscribeDTO(methodName, alarmSubscribeDTO, tenant, workspace, groupWorkspace);
  }

  private LevelAuthorizationCheckResult checkAlarmSubscribeDTO(String methodName,
      AlarmSubscribeDTO alarmSubscribeDTO, String tenant, String workspace,
      String simpleWorkspace) {

    if (alarmSubscribeDTO == null) {
      return successCheckResult();
    }

    if (StringUtils.isNotEmpty(alarmSubscribeDTO.getUniqueId())
        && !checkUniqueId(alarmSubscribeDTO.getUniqueId(), tenant, workspace)) {
      return failCheckResult("invalid uniqueId %s", alarmSubscribeDTO.getUniqueId());
    }

    MonitorUser mu = RequestContext.getContext().mu;
    if (!CollectionUtils.isEmpty(alarmSubscribeDTO.getAlarmSubscribe())) {
      for (AlarmSubscribeInfo alarmSubscribeInfo : alarmSubscribeDTO.getAlarmSubscribe()) {
        boolean update = alarmSubscribeInfo.getId() != null;

        if (update) {
          LevelAuthorizationCheckResult updateCheckResult =
              checkIdExists(alarmSubscribeInfo.getId(), tenant, workspace);
          if (!updateCheckResult.isSuccess()) {
            return updateCheckResult;
          }
        }

        if (StringUtils.isNotEmpty(alarmSubscribeInfo.getTenant())) {
          boolean validTenant =
              parameterSecurityService.checkTenant(alarmSubscribeInfo.getTenant(), tenant);
          if (!validTenant) {
            return failCheckResult("invalid AlarmSubscribe tenant %s",
                alarmSubscribeInfo.getTenant());
          }
        }

        if (StringUtils.isNotEmpty(alarmSubscribeInfo.getWorkspace())
            && !StringUtils.equals(alarmSubscribeInfo.getWorkspace(), workspace)) {
          return failCheckResult("invalid AlarmSubscribe workspace %s",
              alarmSubscribeInfo.getWorkspace());
        }

        if (StringUtils.isNotEmpty(alarmSubscribeInfo.getSubscriber())) {
          boolean validSubscriber;
          if (isDingRobotNoticeType(alarmSubscribeInfo.getNoticeType())) {
            validSubscriber = checkDingRobotSubscriber(alarmSubscribeInfo.getSubscriber(), tenant,
                simpleWorkspace);
          } else {
            validSubscriber = parameterSecurityService
                .checkUserTenantAndWorkspace(alarmSubscribeInfo.getSubscriber(), mu);
          }

          if (!validSubscriber) {
            return failCheckResult("invalid subscriber %s for notice type %s",
                alarmSubscribeInfo.getSubscriber(), J.toJson(alarmSubscribeInfo.getNoticeType()));
          }
        }

        if (alarmSubscribeInfo.getGroupId() != null && alarmSubscribeInfo.getGroupId() > 0) {
          QueryWrapper<AlarmGroup> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("id", alarmSubscribeInfo.getGroupId());
          requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, simpleWorkspace);
          List<AlarmGroup> existedAlarmGroups = this.alarmGroupMapper.selectList(queryWrapper);
          if (CollectionUtils.isEmpty(existedAlarmGroups)) {
            return failCheckResult("invalid AlarmGroup id %s, for tenant %s, workspace %s",
                alarmSubscribeInfo.getGroupId(), tenant, simpleWorkspace);
          }
        }

        if (StringUtils.isNotEmpty(alarmSubscribeInfo.getUniqueId())
            && !checkUniqueId(alarmSubscribeInfo.getUniqueId(), tenant, workspace)) {
          return failCheckResult("invalid uniqueId %s", alarmSubscribeInfo.getUniqueId());
        }

        if (!CollectionUtils.isEmpty(alarmSubscribeInfo.getNoticeType())
            && !checkNoticeType(alarmSubscribeInfo.getNoticeType())) {
          return failCheckResult("invalid noticeType %s",
              J.toJson(alarmSubscribeInfo.getNoticeType()));
        }
      }
    }

    return successCheckResult();
  }

  private boolean checkDingRobotSubscriber(String dingRobotId, String tenant,
      String simpleWorkspace) {
    if (StringUtils.isEmpty(dingRobotId) || !StringUtils.isNumeric(dingRobotId)) {
      return false;
    }
    Long id = Long.parseLong(dingRobotId);
    QueryWrapper<AlarmDingDingRobot> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, simpleWorkspace);

    List<AlarmDingDingRobot> robots = this.dingDingRobotMapper.selectList(queryWrapper);
    return !CollectionUtils.isEmpty(robots);
  }

  private boolean isDingRobotNoticeType(List<String> noticeType) {
    if (CollectionUtils.isEmpty(noticeType)) {
      return false;
    }
    return noticeType.contains("dingDingRobot");
  }

  private boolean checkNoticeType(List<String> noticeTypes) {
    for (String noticeType : noticeTypes) {
      if (!Notice_Type_Set.contains(noticeType)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkUniqueId(String uniqueId, String tenant, String workspace) {
    String[] arr = uniqueId.split("_", 2);
    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("rule_type", arr[0]);
    queryWrapper.eq("id", Long.parseLong(arr[1]));
    requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);
    List<AlarmRule> rules = this.alarmRuleMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(rules)) {
      return false;
    }
    return true;
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmSubscribe> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);

    List<AlarmSubscribe> existedAlarmSubscribes =
        this.alarmSubscribeMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(existedAlarmSubscribes)) {
      return failCheckResult("invalid AlarmSubscribe id %s, for tenant %s, workspace %s", id,
          tenant, workspace);
    }
    return successCheckResult();
  }
}
