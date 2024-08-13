/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import javax.sql.DataSource;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/24
 *
 * @author xzchaoo
 */
@Data
public class StateConfig {
  private Type type = Type.LOCAL;
  private String localBaseDir = "/tmp/state";

  private DataSource dataSource;

  public enum Type {
    /**
     * Local disk based state store. This type can only be used in stand-alone mode.
     */
    LOCAL,
    /**
     * JDBC based state store. This type can be used in multi-node/cluster mode.
     */
    JDBC,
    // OSS
  }
}
