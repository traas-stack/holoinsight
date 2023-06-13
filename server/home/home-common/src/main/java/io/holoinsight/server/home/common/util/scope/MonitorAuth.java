/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import io.holoinsight.server.home.common.util.scope.MonitorAuthPure.AuthTargetPure;
import io.holoinsight.server.home.common.util.scope.MonitorAuthPure.MonitorAuthPkg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorAuth.java, v 0.1 2022年03月14日 5:14 下午 jinsong.yjs Exp $
 */
public class MonitorAuth {
  public static String MONITOR_AUTH = "MONITOR_AUTH";
  public static String ALL_ID = "*";

  // 拥有的权限，为什么是数组？一种情况是：有编辑权限，自然就有查看权限，
  // 一种情况是我有superView, 但是还有 edit;这里应该是展开值
  public Map<AuthTarget, Set<PowerConstants>> powerConstants = new HashMap<>();

  /** 权限的树形补齐，方便做比较 */
  public MonitorAuth treeExtend() {
    MonitorAuth ret = new MonitorAuth();
    Map<AuthTarget, Set<PowerConstants>> newPowers = ret.powerConstants;
    for (Entry<AuthTarget, Set<PowerConstants>> e : powerConstants.entrySet()) {
      AuthTarget authTarget = e.getKey();
      // 原样复制
      newPowers.put(authTarget, e.getValue());
      // 产生子项
      List<AuthTargetType> subTypes = AuthTargetType.getSubType(authTarget.authTargetType);
      for (AuthTargetType subType : subTypes) {
        AuthTarget subTarget = new AuthTarget(subType, ALL_ID);
        Set<PowerConstants> pws = newPowers.computeIfAbsent(subTarget, k -> new HashSet<>());
        pws.addAll(e.getValue()); // 权限不变依次继承到子项
      }
    }
    return ret;
  }

  public Map<String, Set<PowerConstants>> getTenantViewPowerList() {
    Map<String, Set<PowerConstants>> ts = new HashMap<>();
    for (Entry<AuthTarget, Set<PowerConstants>> pe : powerConstants.entrySet()) {
      if (pe.getKey().authTargetType.equals(AuthTargetType.TENANT)) {
        ts.put(pe.getKey().quthTargetId, pe.getValue());
      }
    }
    return ts;
  }

  public Set<String> hasTenantViewPowerList() {
    Set<String> ts = new HashSet<>();
    for (Entry<AuthTarget, Set<PowerConstants>> pe : powerConstants.entrySet()) {
      if (pe.getKey().authTargetType.equals(AuthTargetType.TENANT)) {
        ts.add(pe.getKey().quthTargetId);
      }
    }
    return ts;
  }

  public Set<String> hasWsViewPowerList() {
    Set<String> ws = new HashSet<>();
    for (Entry<AuthTarget, Set<PowerConstants>> pe : powerConstants.entrySet()) {
      if (pe.getKey().authTargetType.equals(AuthTargetType.WORKSPACE)) {
        ws.add(pe.getKey().quthTargetId);
      }
    }
    return ws;
  }

  public void mergeMonitorAuth(MonitorAuth ma) {
    for (Entry<AuthTarget, Set<PowerConstants>> e : ma.powerConstants.entrySet()) {
      Set<PowerConstants> pcs =
          this.powerConstants.computeIfAbsent(e.getKey(), k -> new HashSet<>());
      pcs.addAll(e.getValue());
    }
  }

  public static MonitorAuth mergeMonitorAuth(List<MonitorAuth> mas) {
    MonitorAuth ret = new MonitorAuth();
    for (MonitorAuth ma : mas) {
      ret.mergeMonitorAuth(ma);
    }
    return ret;
  }

  public MonitorAuthPure toPure() {
    MonitorAuthPure map = new MonitorAuthPure();
    for (Entry<AuthTarget, Set<PowerConstants>> e : this.powerConstants.entrySet()) {
      AuthTargetPure atp = new AuthTargetPure();
      atp.id = e.getKey().quthTargetId;
      atp.at = e.getKey().authTargetType.name();
      Set<String> pcPure = new HashSet<>();
      for (PowerConstants pc : e.getValue()) {
        pcPure.add(pc.name());
      }
      MonitorAuthPkg pkg = new MonitorAuthPkg();
      pkg.atp = atp;
      pkg.pcs = pcPure;
      map.pkgs.add(pkg);
    }
    return map;
  }
}
