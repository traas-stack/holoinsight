/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.ula;

import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorParams;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorTenant;
import io.holoinsight.server.home.common.util.scope.MonitorUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: ULAFacade.java, v 0.1 2022年03月15日 6:02 下午 jinsong.yjs Exp $
 */
public interface ULA {

  String name();

  IdentityType type();

  /**
   * LOGIN
   */
  String getLoginUrl();

  // 分析好cookie, 取回监控 user
  MonitorUser checkLogin(HttpServletRequest req, HttpServletResponse resp);

  // 登出，清理好之前留下的cookie, 并重定向回登陆页
  void logout(HttpServletRequest req, HttpServletResponse resp);

  /**
   * USER
   */
  // 精确获取用户信息
  MonitorUser getByAutoToken(String authToken);

  MonitorUser getByLoginName(String loginName);

  MonitorUser getByUserId(String userId);

  // 获取用户下面租户信息
  List<MonitorTenant> getUserTenants(MonitorUser user);

  /**
   * AUTH
   */
  // 查看一个租户下的权限
  MonitorAuth getUserPowerPkg(HttpServletRequest req, MonitorUser user, MonitorScope ms);

  // 是否是super用户
  void checkSuper(MonitorUser user) throws Throwable;

  // 获取租户下所有用户信息信息
  List<MonitorUser> getUsers(MonitorUser user, MonitorScope ms);

  Set<String> getUserIds(MonitorUser user, MonitorScope ms);

  // 登陆跳转token
  String authTokenName();

  // 权限
  Boolean checkWorkspace(HttpServletRequest request, MonitorUser user, MonitorScope ms,
      MonitorParams mp);

  Boolean authFunc(HttpServletRequest request);

  // 权限申请跳转链接
  String authApplyUrl();

  MonitorScope getMonitorScope(HttpServletRequest req, MonitorUser mu);

  MonitorParams getMonitorParams(HttpServletRequest req, MonitorUser mu);

}
