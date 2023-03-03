/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author jiwliu
 * @version : Column.java, v 0.1 2022年10月11日 19:15 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModelColumn {
  private String name;
  private Class<?> type;
  private Type genericType;
}
