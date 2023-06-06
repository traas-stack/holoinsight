/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.ula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;

/**
 *
 * @author jsy1001de
 * @version 1.0: ULAFacade.java, v 0.1 2022年03月15日 6:05 下午 jinsong.yjs Exp $
 */
@Service
public class ULAFacade {
  private Map<String, ULA> instanceMap = new HashMap<>();
  private UserCache userCache = new UserCache();
  @Autowired(required = false)
  private List<ULA> ulaList = new ArrayList<>();

  @PostConstruct
  public void init() {
    for (ULA ula : ulaList) {
      instanceMap.put(ula.name(), ula);
    }
  }

  public MonitorUser getByLoginNameWithCache(String loginName, String tenant) throws Throwable {
    MonitorUser mu = userCache.get(loginName, tenant);
    if (mu == null) {
      return getByLoginName(loginName);
    } else {
      return mu;
    }
  }

  public MonitorUser getByLoginName(String loginName) throws Throwable {
    return getCurrentULA().getByLoginName(loginName);
  }

  // 取某个scope的最高权限
  public MonitorAuth getUserPowerPkg(MonitorUser user, MonitorScope ms) throws Throwable {
    return getCurrentULA().getUserPowerPkg(user, ms);
  }

  // 分析好cookie, 取回监控 user, 优先走本地校验
  public MonitorUser checkLogin(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
    return getCurrentULA().checkLogin(req, resp);
  }

  public void logout(HttpServletRequest req, HttpServletResponse resp) {
    getCurrentULA().logout(req, resp);
  }

  public ULA getCurrentULA() {
    String ulaType = MetaDictUtil.getUlaType();
    return instanceMap.get(ulaType);
  }

  public Boolean authFunc(HttpServletRequest req) {
    return getCurrentULA().authFunc(req);
  }

  public MonitorScope getMonitorScope(HttpServletRequest req, MonitorUser mu) {
    return getCurrentULA().getMonitorScope(req, mu);
  }

}
