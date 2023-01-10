/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/11/1
 *
 * @author zzhb101
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ConnectionInfo {
  private String registry;
}
