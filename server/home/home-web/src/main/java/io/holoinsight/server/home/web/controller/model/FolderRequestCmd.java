/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderRequestCmd.java, v 0.1 2022年05月30日 2:02 下午 jinsong.yjs Exp $
 */
public class FolderRequestCmd {
  // folder的路径查询，或者custom pluginId的查询， 两个都存在时优先搞customPluginId
  public Long folderId;
  public Long customPluginId;

  public boolean includePluginName = false;
}
