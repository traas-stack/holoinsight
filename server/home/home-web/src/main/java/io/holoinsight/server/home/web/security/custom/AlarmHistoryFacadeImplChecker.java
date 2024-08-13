/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.mapper.AlarmHistoryMapper;
import io.holoinsight.server.common.dao.entity.AlarmHistory;
import io.holoinsight.server.common.dao.entity.dto.AlarmHistoryDTO;
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

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-02-07 17:25:00
 */
@Slf4j
@Service
public class AlarmHistoryFacadeImplChecker
    implements AbstractResourceChecker, LevelAuthorizationCheck {

  @Resource
  private AlarmHistoryMapper alarmHistoryMapper;
  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    String workspace =
        this.requestContextAdapter.getWorkspaceFromContext(RequestContext.getContext());
    String tenant = this.requestContextAdapter.getTenantFromContext(RequestContext.getContext());

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace) {
    switch (methodName) {
      case "queryById":
        return checkIdNotNull(parameters);
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
      case "pageQuery":
        return checkPageRequest(methodName, parameters, tenant, workspace);
      default:
        return successCheckResult();
    }
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmHistory> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);

    List<AlarmHistory> exist = this.alarmHistoryMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      return failCheckResult("fail to check id for no existed %s %s %s", id, tenant, workspace);
    }
    return successCheckResult();
  }

  protected LevelAuthorizationCheckResult checkPageRequest(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlarmHistoryDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlarmHistoryDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %d larger than end %d",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    AlarmHistoryDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkAlarmHistoryDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmHistoryDTO(String methodName,
      AlarmHistoryDTO target, String tenant, String workspace) {
    if (StringUtils.isNotEmpty(target.getTenant())
        && !StringUtils.equals(target.getTenant(), tenant)) {
      return failCheckResult("fail to check %s for invalid tenant %s, valid tenant %s", methodName,
          target.getTenant(), tenant);
    }

    if (StringUtils.isNotEmpty(target.getWorkspace())
        && !StringUtils.equals(target.getWorkspace(), workspace)) {
      return failCheckResult("fail to check %s for invalid workspace %s, valid workspace %s",
          methodName, target.getWorkspace(), workspace);
    }

    return successCheckResult();
  }
}
