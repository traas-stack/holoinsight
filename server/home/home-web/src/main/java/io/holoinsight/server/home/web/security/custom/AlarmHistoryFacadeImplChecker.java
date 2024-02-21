/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author masaimu
 * @version 2024-02-07 17:25:00
 */
@Slf4j
@Service
public class AlarmHistoryFacadeImplChecker extends AbstractResourceChecker {

  @Resource
  private AlarmHistoryMapper alarmHistoryMapper;
  @Autowired
  private RequestContextAdapter requestContextAdapter;

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

  @Override
  boolean checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmHistory> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);

    List<AlarmHistory> exist = this.alarmHistoryMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      log.error("fail to check id for no existed {} {} {}", id, tenant, workspace);
      return false;
    }
    return true;
  }

  private boolean checkPageRequest(String methodName, List<String> parameters, String tenant,
      String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return false;
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlarmHistoryDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlarmHistoryDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        log.error("fail to check time range for start {} larger than end {}", pageRequest.getFrom(),
            pageRequest.getTo());
        return false;
      }
    }

    AlarmHistoryDTO target = pageRequest.getTarget();
    if (target == null) {
      log.error("fail to check target, target can not be null");
      return false;
    }
    return checkAlarmHistoryDTO(methodName, target, tenant, workspace);
  }

  private boolean checkAlarmHistoryDTO(String methodName, AlarmHistoryDTO target, String tenant,
      String workspace) {
    if (StringUtils.isNotEmpty(target.getTenant())
        && !StringUtils.equals(target.getTenant(), tenant)) {
      log.error("fail to check {} for invalid tenant {}, valid tenant {}", methodName,
          target.getTenant(), tenant);
      return false;
    }

    if (StringUtils.isNotEmpty(target.getWorkspace())
        && !StringUtils.equals(target.getWorkspace(), workspace)) {
      log.error("fail to check {} for invalid workspace {}, valid workspace {}", methodName,
          target.getWorkspace(), workspace);
      return false;
    }

    return true;
  }
}
