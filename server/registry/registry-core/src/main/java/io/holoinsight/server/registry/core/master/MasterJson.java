/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2023/8/29
 *
 * @author xzchaoo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterJson {
  String id;
  String payload;
}
