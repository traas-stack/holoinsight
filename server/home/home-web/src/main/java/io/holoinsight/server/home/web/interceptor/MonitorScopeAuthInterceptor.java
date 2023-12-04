/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.interceptor;

import java.lang.reflect.Method;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTarget;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorScopeAuthInterceptor.java, v 0.1 2022年03月14日 5:55 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class MonitorScopeAuthInterceptor implements MethodInterceptor {
  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    try {

      Context context = RequestContext.getContext();
      MonitorScope ms = context.ms;
      MonitorUser mu = context.mu;
      MonitorAuth ma = context.ma;

      if (mu.isSuper() || mu.getIdentityType() == IdentityType.OUTTOKEN) {
        return methodInvocation.proceed();
      }

      Method method = AopUtils.getMostSpecificMethod(methodInvocation.getMethod(),
          methodInvocation.getThis().getClass());

      MonitorScopeAuth monitorScopeAuth =
          AnnotationUtils.findAnnotation(method, MonitorScopeAuth.class);

      assert monitorScopeAuth != null;
      PowerConstants needPower = monitorScopeAuth.needPower();
      if (needPower == PowerConstants.NO_AUTH || mu.isSuper()) {
        return methodInvocation.proceed();
      }
      AuthTargetType authType = monitorScopeAuth.targetType();

      if (authType == AuthTargetType.SRE && mu.getIdentityType() != IdentityType.OUTTOKEN) {
        JsonResult<Object> resp = new JsonResult<>();
        JsonResult.fillFailResultTo(resp, ResultCodeEnum.NO_ROLE_AUTH.getResultCode(), "no auth");
        return resp;
      }

      ma = ma.treeExtend(); // 扩展好，准备检测
      AuthTargetType checkAuthType = getRealAuthType(authType, ms);

      boolean pass = authCheckByType(checkAuthType, needPower, ma, ms, mu);

      if (!pass) {
        log.warn("monitor tenant auth not enough, need power: " + needPower + ", ma:" + J.toJson(ma)
            + ", mu:" + J.toJson(mu) + ",ms:" + J.toJson(ms));
        JsonResult<Object> failResult =
            JsonResult.createFailResult("permission denied, need auth: " + needPower);
        failResult.setResultCode(ResultCodeEnum.NO_ROLE_AUTH.getResultCode());
        return failResult;
      }
    } catch (Exception e) {
      log.error("auth failed, " + e.getMessage(), e);
      throw e;
    }

    return methodInvocation.proceed();
  }

  private AuthTargetType getRealAuthType(AuthTargetType authType, MonitorScope ms) {
    AuthTargetType checkAuthType = authType;
    if (authType.equals(AuthTargetType.CONTEXT)) {
      // 上下文校验, 精准定位到是哪个对象, 从下往上
      if (MonitorScope.legalValue(ms.tenant)) {
        checkAuthType = AuthTargetType.TENANT;
      }
    }
    // 其他的需要谁的权限，就搞谁
    return checkAuthType;
  }

  boolean authCheckByType(AuthTargetType authType, PowerConstants needPower, MonitorAuth ma,
      MonitorScope ms, MonitorUser mu) {
    // 超级管理员优先过
    if (superCheck(mu, needPower)) {
      return true;
    }
    if (authType.equals(AuthTargetType.SITE)) {
      // 站点校验
      return true;// authCheck(new AuthTarget(AuthTargetType.SITE, ms.siteId), needPower, ma);
    } else if (authType.equals(AuthTargetType.TENANT)) {
      return authCheck(new AuthTarget(AuthTargetType.TENANT, ms.tenant), needPower, ma);

    }
    return false;
  }

  private boolean authCheck(AuthTarget at, PowerConstants needPower, MonitorAuth ma) {
    // auth check
    Set<PowerConstants> pcs = ma.powerConstants.get(at);
    boolean paas = needPower.powerEnough(pcs);
    if (paas) {
      return true;
    }
    // 尝试获取继承下来的权限
    at = new AuthTarget(at.getAuthTargetType(), MonitorAuth.ALL_ID);
    pcs = ma.powerConstants.get(at);
    paas = needPower.powerEnough(pcs);
    if (paas) {
      return true;
    }
    return false;
  }

  public boolean superCheck(MonitorUser mu, PowerConstants needPower) {
    PowerConstants p = PowerConstants.NO_AUTH;
    // 从高往低取权限
    if (mu.isSuperAdmin()) {
      p = PowerConstants.SUPER_ADMIN;
    } else if (mu.isSuperViewer()) {
      p = PowerConstants.SUPER_VIEW;
    }
    return needPower.powerEnough(p);
  }
}
