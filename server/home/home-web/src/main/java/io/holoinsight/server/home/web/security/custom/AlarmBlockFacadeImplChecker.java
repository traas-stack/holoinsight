/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.mapper.AlarmBlockMapper;
import io.holoinsight.server.common.dao.mapper.AlarmRuleMapper;
import io.holoinsight.server.common.dao.entity.AlarmBlock;
import io.holoinsight.server.common.dao.entity.AlarmRule;
import io.holoinsight.server.common.dao.entity.dto.AlarmBlockDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-03-26 21:05:00
 */
@Slf4j
@Service
public class AlarmBlockFacadeImplChecker
    implements AbstractResourceChecker, LevelAuthorizationCheck {

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  @Resource
  private AlarmBlockMapper alarmBlockMapper;
  @Resource
  private AlarmRuleMapper alarmRuleMapper;

  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    RequestContext.Context context = RequestContext.getContext();
    String workspace = this.requestContextAdapter.getWorkspaceFromContext(context);
    String tenant = this.requestContextAdapter.getTenantFromContext(context);

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace) {
    switch (methodName) {
      case "create":
      case "update":
        return checkAlarmBlockDTO(methodName, parameters, tenant, workspace);
      case "queryById":
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
      case "pageQuery":
        return checkPageRequest(methodName, parameters, tenant, workspace);
      case "queryByRuleId":
        return checkQueryByRuleId(methodName, parameters, tenant, workspace);
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
    MonitorPageRequest<AlarmBlockDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlarmBlockDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %d larger than end %d",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    AlarmBlockDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkAlarmBlockDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkQueryByRuleId(String methodName,
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

  private LevelAuthorizationCheckResult checkAlarmBlockDTO(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlarmBlockDTO alarmBlockDTO = J.fromJson(parameters.get(0), AlarmBlockDTO.class);
    return checkAlarmBlockDTO(methodName, alarmBlockDTO, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmBlockDTO(String methodName,
      AlarmBlockDTO alarmBlockDTO, String tenant, String workspace) {
    if (methodName.equals("create")) {
      if (alarmBlockDTO.getId() != null) {
        return failCheckResult("fail to check %s for id is not null", methodName);
      }
    }

    if (methodName.equals("update")) {
      if (alarmBlockDTO.getId() == null) {
        return failCheckResult("fail to check %s for id is null", methodName);
      }
      LevelAuthorizationCheckResult updateCheckResult =
          checkIdExists(alarmBlockDTO.getId(), tenant, workspace);
      if (!updateCheckResult.isSuccess()) {
        return updateCheckResult;
      }
    }

    if (StringUtils.isNotEmpty(alarmBlockDTO.getExtra())) {
      return failCheckResult("extra should be empty");
    }
    if (StringUtils.isNotEmpty(alarmBlockDTO.getTags()) && !checkTags(alarmBlockDTO.getTags())) {
      return failCheckResult("invalid tags %s", alarmBlockDTO.getTags());
    }
    if (StringUtils.isNotEmpty(alarmBlockDTO.getTenant())
        && !StringUtils.equals(alarmBlockDTO.getTenant(), tenant)) {
      return failCheckResult("invalid tenant %s, real tenant %s", alarmBlockDTO.getTenant(),
          tenant);
    }
    if (StringUtils.isNotEmpty(alarmBlockDTO.getWorkspace())
        && !StringUtils.equals(alarmBlockDTO.getWorkspace(), workspace)) {
      return failCheckResult("invalid workspace %s, real workspace %s",
          alarmBlockDTO.getWorkspace(), workspace);
    }
    if (StringUtils.isNotEmpty(alarmBlockDTO.getReason())) {
      return failCheckResult("reason should be empty");
    }
    if (StringUtils.isNotEmpty(alarmBlockDTO.getUniqueId())
        && !checkUniqueId(alarmBlockDTO.getUniqueId(), tenant, workspace)) {
      return failCheckResult("invalid uniqueId %s", alarmBlockDTO.getUniqueId());
    }
    return successCheckResult();
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

  private boolean checkTags(String tags) {
    if (StringUtils.isEmpty(tags)) {
      return true;
    }
    try {
      Map<String, String> tagMap =
          J.fromJson(tags, new TypeToken<Map<String, String>>() {}.getType());
      for (Map.Entry<String, String> entry : tagMap.entrySet()) {
        if (!checkSqlField(entry.getKey()) || !checkSqlField(entry.getValue())) {
          return false;
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmBlock> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);
    List<AlarmBlock> exist = this.alarmBlockMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      return failCheckResult("fail to check id for no existed %d %s %s", id, tenant, workspace);
    }
    return successCheckResult();
  }
}
