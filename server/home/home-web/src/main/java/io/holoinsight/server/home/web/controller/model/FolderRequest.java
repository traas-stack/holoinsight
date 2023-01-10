/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderRequest.java, v 0.1 2022年05月30日 2:05 下午 jinsong.yjs Exp $
 */
public class FolderRequest implements Serializable {
  private static final long serialVersionUID = -4200988599031537998L;
  public List<FolderRequestCmd> requests;
}
