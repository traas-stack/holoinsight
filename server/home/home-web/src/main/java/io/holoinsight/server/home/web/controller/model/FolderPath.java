/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderPath.java, v 0.1 2022年05月30日 2:06 下午 jinsong.yjs Exp $
 */
@Data
public class FolderPath implements Serializable {
  private static final long serialVersionUID = -783815229535552853L;
  private Long id;
  private String name;
  private String type = FOLDER;

  public static final String FILE = "file";
  public static final String FOLDER = "folder";

  public FolderPath(Long id, String name) {
    super();
    this.id = id;
    this.name = name;
  }

  public FolderPath(Long id, String name, String type) {
    super();
    this.id = id;
    this.name = name;
    this.type = type;
  }
}
