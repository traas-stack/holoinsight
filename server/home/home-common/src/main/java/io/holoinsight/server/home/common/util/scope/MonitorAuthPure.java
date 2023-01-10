/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorAuthPure.java, v 0.1 2022年05月19日 6:04 下午 jinsong.yjs Exp $
 */
public class MonitorAuthPure {

  public Long exprie;

  public List<MonitorAuthPkg> pkgs = new ArrayList<>();

  public static class MonitorAuthPkg {
    public AuthTargetPure atp;
    public Set<String> pcs = new HashSet<>();
  }

  public static class AuthTargetPure {
    public String at;
    public String id;
  }

  public MonitorAuth toAdv() {
    MonitorAuth ma = new MonitorAuth();
    for (MonitorAuthPkg pkg : pkgs) {
      AuthTarget at = new AuthTarget(AuthTargetType.valueOf(pkg.atp.at), pkg.atp.id);
      Set<PowerConstants> pcs = new HashSet<>();
      for (String pcPure : pkg.pcs) {
        pcs.add(PowerConstants.valueOf(pcPure));
      }
      ma.powerConstants.put(at, pcs);
    }
    return ma;
  }
}
