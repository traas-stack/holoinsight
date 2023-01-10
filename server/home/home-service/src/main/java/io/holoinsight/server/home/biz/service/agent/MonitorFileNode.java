/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.agent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorFileNode.java, v 0.1 2022年04月24日 12:45 下午 jinsong.yjs Exp $
 */
public class MonitorFileNode {
  private String name;
  private boolean dir;
  private List<MonitorFileNode> subs = new ArrayList<>();
  private String fullPath;

  public MonitorFileNode() {}

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDir() {
    return this.dir;
  }

  public void setDir(boolean dir) {
    this.dir = dir;
  }

  public List<MonitorFileNode> getSubs() {
    return this.subs;
  }

  public void setSubs(List<MonitorFileNode> subs) {
    this.subs = subs;
  }

  public String getFullPath() {
    return this.fullPath;
  }

  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }
}
