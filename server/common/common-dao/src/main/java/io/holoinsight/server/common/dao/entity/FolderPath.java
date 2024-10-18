/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author limengyang
 * @version FolderPath.java, v 0.1 2024年09月20日 11:14 limengyang
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
