/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.ula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.holoinsight.server.home.biz.service.UserinfoService;
import io.holoinsight.server.home.common.util.scope.MonitorParams;
import io.holoinsight.server.home.facade.UserinfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.scope.MonitorAuth;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import org.springframework.util.CollectionUtils;

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

  @Autowired
  private UserinfoService userinfoService;

  @PostConstruct
  public void init() {
    for (ULA ula : ulaList) {
      instanceMap.put(ula.name(), ula);
    }
  }

  public MonitorUser getByLoginNameWithCache(String loginName, String tenant) {
    MonitorUser mu = userCache.get(loginName, tenant);
    if (mu == null) {
      return getByLoginName(loginName);
    } else {
      return mu;
    }
  }

  public MonitorUser getByLoginName(String loginName) {
    MonitorUser mu = getCurrentULA().getByLoginName(loginName);

    return addUserinfo(mu);
  }

  private MonitorUser addUserinfo(MonitorUser mu) {
    if (mu == null || StringUtils.isBlank(mu.getUserId())) {
      return mu;
    }
    if (StringUtils.isNotEmpty(mu.getMobile()) && StringUtils.isNotEmpty(mu.getEmail())) {
      return mu;
    }
    UserinfoDTO dto = this.userinfoService.queryByUid(mu.getUserId(), mu.getLoginTenant());
    if (dto == null) {
      return mu;
    }
    if (StringUtils.isEmpty(mu.getMobile())) {
      mu.setMobile(dto.getPhoneNumberAlias());
    }
    if (StringUtils.isEmpty(mu.getEmail())) {
      mu.setEmail(dto.getEmailAlias());
    }
    return mu;
  }

  public MonitorUser getByUserId(String userId) {
    MonitorUser mu = getCurrentULA().getByUserId(userId);
    return addUserinfo(mu);
  }

  public List<MonitorUser> getUsers(MonitorUser user, MonitorScope ms) {
    List<MonitorUser> muList = getCurrentULA().getUsers(user, ms);
    if (CollectionUtils.isEmpty(muList)) {
      return Collections.emptyList();
    }
    List<String> uidList = muList.stream() //
        .filter(mu -> StringUtils.isEmpty(mu.getMobile()) || StringUtils.isEmpty(mu.getEmail())) //
        .map(MonitorUser::getUserId) //
        .collect(Collectors.toList());
    Map<String, UserinfoDTO> dtos = this.userinfoService.queryByUid(uidList, ms.getTenant());

    for (MonitorUser mu : muList) {
      UserinfoDTO dto = dtos.get(mu.getUserId());
      if (dto == null) {
        continue;
      }

      if (StringUtils.isEmpty(mu.getMobile())) {
        mu.setMobile(dto.getPhoneNumberAlias());
      }
      if (StringUtils.isEmpty(mu.getEmail())) {
        mu.setEmail(dto.getEmailAlias());
      }
    }
    return muList;
  }

  // 取某个scope的最高权限
  public MonitorAuth getUserPowerPkg(HttpServletRequest req, MonitorUser user, MonitorScope ms)
      throws Throwable {
    return getCurrentULA().getUserPowerPkg(req, user, ms);
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

  public void checkWorkspace(HttpServletRequest req, MonitorUser user, MonitorScope ms,
      MonitorParams mp) {
    getCurrentULA().checkWorkspace(req, user, ms, mp);
  }

  public Boolean authFunc(HttpServletRequest req) {
    return getCurrentULA().authFunc(req);
  }

  public MonitorScope getMonitorScope(HttpServletRequest req, MonitorUser mu) {
    return getCurrentULA().getMonitorScope(req, mu);
  }

  public MonitorParams getMonitorParams(HttpServletRequest req, MonitorUser mu) {
    return getCurrentULA().getMonitorParams(req, mu);
  }

}
