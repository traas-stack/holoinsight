/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderPaths.java, v 0.1 2022年05月30日 2:06 下午 jinsong.yjs Exp $
 */
@Data
public class FolderPaths implements Serializable {
  private static final long serialVersionUID = 685496251839004159L;
  public List<FolderPath> paths = new ArrayList<FolderPath>();

}
