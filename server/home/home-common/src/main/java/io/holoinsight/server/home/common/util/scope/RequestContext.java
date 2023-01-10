/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: RequestContext.java, v 0.1 2022年03月14日 5:09 下午 jinsong.yjs Exp $
 */
public class RequestContext {
  static ThreadLocal<Context> gs = new ThreadLocal<Context>();

  public static void setContext(Context c) {
    gs.set(c);
  }

  public static Context getContext() {
    return gs.get();
  }

  public static void setMonitorParams(MonitorParams mp) {
    gs.get().mp = mp;
  }

  public static class Context {
    public MonitorScope ms;
    public MonitorUser mu;
    public MonitorAuth ma;
    public MonitorParams mp;

    public Context(MonitorScope ms) {
      super();
      this.ms = ms;
      this.mp = new MonitorParams();
    }

    public Context(MonitorScope ms, MonitorUser mu, MonitorAuth ma) {
      super();
      this.ms = ms;
      this.mu = mu;
      this.ma = ma;
      this.mp = new MonitorParams();
    }

    public Context(MonitorScope ms, MonitorUser mu, MonitorAuth ma, MonitorParams mp) {
      super();
      this.ms = ms;
      this.mu = mu;
      this.ma = ma;
      this.mp = mp;
    }

    public Map<String, Object> buildScopeContext() {
      Map<String, Object> queryCondition = new HashMap<String, Object>();
      queryCondition.put("tenantId", ms.getTenant());
      return queryCondition;
    }

  }

}
