/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹路径
 * 
 * @author limengyang
 * @version FolderPaths.java, v 0.1 2024年09月20日 10:09 limengyang
 */
@Data
public class FolderPaths implements Serializable {
  private static final long serialVersionUID = 685496251839004159L;
  public List<FolderPath> paths = new ArrayList<FolderPath>();
}
