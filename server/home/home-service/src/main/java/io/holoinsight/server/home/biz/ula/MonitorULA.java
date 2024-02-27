/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.ula;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.common.service.TenantService;
import io.holoinsight.server.home.common.util.scope.AuthTarget;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorParams;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorTenant;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.common.dao.entity.dto.TenantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorULA.java, v 0.1 2022年03月15日 6:04 下午 jinsong.yjs Exp $
 */
@Service
public class MonitorULA implements ULA {

  @Autowired
  private TenantService tenantService;

  @Override
  public String name() {
    return IdentityType.MONITOR.name();
  }

  @Override
  public IdentityType type() {
    return IdentityType.MONITOR;
  }

  @Override
  public String getLoginUrl() {
    return MetaDictUtil.getUlaHost();
  }

  @Override
  public MonitorUser checkLogin(HttpServletRequest req, HttpServletResponse resp) {
    return getByAutoToken(null);
  }

  @Override
  public void logout(HttpServletRequest req, HttpServletResponse resp) {
    MonitorCookieUtil.removeUserCookie(resp);
  }

  @Override
  public MonitorUser getByAutoToken(String authToken) {

    MonitorUser adminUser = MonitorUser.adminUser;
    List<MonitorTenant> userTenants = getUserTenants(adminUser);
    adminUser.setLoginTenant(userTenants.get(0).getCode());

    return adminUser;
  }

  @Override
  public MonitorUser getByLoginName(String loginName) {
    return MonitorUser.adminUser;
  }

  @Override
  public MonitorUser getByUserId(String userId) {
    return MonitorUser.adminUser;
  }

  @Override
  public List<MonitorTenant> getUserTenants(MonitorUser user) {
    List<MonitorTenant> tenants = new ArrayList<>();

    List<TenantDTO> tenantDTOS = tenantService.queryAll();

    if (CollectionUtils.isEmpty(tenantDTOS))
      return tenants;

    tenantDTOS.forEach(tenantDTO -> {
      tenants.add(new MonitorTenant(tenantDTO.getCode(), tenantDTO.getName()));
    });

    return tenants;
  }

  @Override
  public MonitorAuth getUserPowerPkg(HttpServletRequest req, MonitorUser user, MonitorScope ms) {

    String tenant = ms.getTenant();
    if (null == tenant) {
      List<TenantDTO> tenantDTOList = tenantService.queryAll();
      if (!CollectionUtils.isEmpty(tenantDTOList)) {
        tenant = tenantDTOList.get(0).code;
      }
    }

    Map<AuthTarget, Set<PowerConstants>> tPowers = new HashMap<>();
    tPowers.put(new AuthTarget(AuthTargetType.TENANT, tenant),
        new HashSet<>(Arrays.asList(PowerConstants.VIEW, PowerConstants.EDIT)));

    MonitorAuth monitorAuth = new MonitorAuth();
    monitorAuth.powerConstants = tPowers;
    return monitorAuth;
  }

  @Override
  public void checkSuper(MonitorUser user) throws Throwable {

  }

  @Override
  public List<MonitorUser> getUsers(MonitorUser user, MonitorScope ms) {
    List<MonitorUser> users = new ArrayList<>();
    users.add(MonitorUser.adminUser);
    return users;
  }

  @Override
  public Set<String> getUserIds(MonitorUser user, MonitorScope ms) {
    return null;
  }

  @Override
  public String authTokenName() {
    return "authToken";
  }

  @Override
  public Boolean checkWorkspace(HttpServletRequest request, MonitorUser user, MonitorScope ms,
      MonitorParams mp) {
    return true;
  }

  @Override
  public Boolean authFunc(HttpServletRequest request) {
    return true;
  }

  @Override
  public String authApplyUrl() {
    return "";
  }

  @Override
  public MonitorScope getMonitorScope(HttpServletRequest req, MonitorUser mu) {
    return MonitorCookieUtil.getScope(req, mu);
  }

  @Override
  public MonitorParams getMonitorParams(HttpServletRequest req, MonitorUser mu) {
    return new MonitorParams();
  }
}
