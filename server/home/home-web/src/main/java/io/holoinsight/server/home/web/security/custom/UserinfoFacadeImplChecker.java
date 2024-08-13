/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.entity.Userinfo;
import io.holoinsight.server.common.dao.entity.UserinfoVerification;
import io.holoinsight.server.common.dao.entity.dto.UserinfoDTO;
import io.holoinsight.server.common.dao.mapper.UserinfoMapper;
import io.holoinsight.server.common.dao.mapper.UserinfoVerificationMapper;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.home.biz.ula.ULAFacade;
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
import java.util.Set;

import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-06-03 17:18:00
 */
@Slf4j
@Service
public class UserinfoFacadeImplChecker implements AbstractResourceChecker, LevelAuthorizationCheck {

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  @Autowired
  private ULAFacade ulaFacade;
  @Resource
  private UserinfoVerificationMapper verificationMapper;

  @Autowired
  private UserinfoMapper userinfoMapper;

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
        return checkUserinfoDTO(methodName, parameters, tenant, workspace);
      case "queryById":
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
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
    MonitorPageRequest<UserinfoDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<UserinfoDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %d larger than end %d",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    UserinfoDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkUserinfoDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkUserinfoDTO(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    UserinfoDTO userinfoDTO = J.fromJson(parameters.get(0), UserinfoDTO.class);
    return checkUserinfoDTO(methodName, userinfoDTO, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkUserinfoDTO(String methodName, UserinfoDTO userinfoDTO,
      String tenant, String workspace) {
    if (methodName.equals("create")) {
      if (userinfoDTO.getId() != null) {
        return failCheckResult("fail to check %s for id is not null", methodName);
      }
    }

    if (methodName.equals("update")) {
      if (userinfoDTO.getId() == null) {
        return failCheckResult("fail to check %s for id is null", methodName);
      }
      LevelAuthorizationCheckResult updateCheckResult =
          checkIdExists(userinfoDTO.getId(), tenant, workspace);
      if (!updateCheckResult.isSuccess()) {
        return updateCheckResult;
      }
    }

    if (StringUtils.isNotEmpty(userinfoDTO.getNickname())
        && !checkSqlName(userinfoDTO.getNickname())) {
      return failCheckResult("invalid nickname %s, please use a-z A-Z 0-9 Chinese - _ , . spaces",
          userinfoDTO.getNickname());
    }

    if (StringUtils.isNotEmpty(userinfoDTO.getUid()) && !checkUid(userinfoDTO.getUid())) {
      return failCheckResult("%s is not in current tenant scope.", userinfoDTO.getUid());
    }
    if (StringUtils.isNotEmpty(userinfoDTO.getTenant())
        && !StringUtils.equals(userinfoDTO.getTenant(), tenant)) {
      return failCheckResult("invalid tenant %s, real tenant %s", userinfoDTO.getTenant(), tenant);
    }
    if (StringUtils.isNotEmpty(userinfoDTO.getWorkspace())
        && !StringUtils.equals(userinfoDTO.getWorkspace(), workspace)) {
      return failCheckResult("invalid workspace %s, real workspace %s", userinfoDTO.getWorkspace(),
          workspace);
    }

    if (!completeUserinfoVerification(userinfoDTO)) {
      return failCheckResult("incomplete UserinfoVerification %d %s",
          userinfoDTO.getUserinfoVerificationId(), userinfoDTO.getUserinfoVerificationCode());
    }

    if (userinfoDTO.getUserinfoVerificationId() != null) {
      QueryWrapper<UserinfoVerification> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("id", userinfoDTO.getUserinfoVerificationId());
      requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);
      List<UserinfoVerification> list = this.verificationMapper.selectList(queryWrapper);
      if (CollectionUtils.isEmpty(list)) {
        return failCheckResult("invalid UserinfoVerification id %d",
            userinfoDTO.getUserinfoVerificationId());
      }
      UserinfoVerification userinfoVerification = list.get(0);
      if (!StringUtils.equals(userinfoVerification.getCode(),
          userinfoDTO.getUserinfoVerificationCode())) {
        return failCheckResult("invalid UserinfoVerification code %s",
            userinfoDTO.getUserinfoVerificationCode());
      }
    }
    return successCheckResult();
  }

  private boolean completeUserinfoVerification(UserinfoDTO userinfoDTO) {
    return (userinfoDTO.getUserinfoVerificationId() == null
        && StringUtils.isEmpty(userinfoDTO.getUserinfoVerificationCode())) //
        || (userinfoDTO.getUserinfoVerificationId() != null
            && StringUtils.isNotEmpty(userinfoDTO.getUserinfoVerificationCode()));
  }

  private boolean checkUid(String uid) {
    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;

    Set<String> userIds = ulaFacade.getCurrentULA().getUserIds(mu, ms);
    return !CollectionUtils.isEmpty(userIds) && userIds.contains(uid);
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);
    List<Userinfo> list = this.userinfoMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(list)) {
      return failCheckResult("invalid UserinfoVerification id %d", id);
    }
    return successCheckResult();
  }
}
