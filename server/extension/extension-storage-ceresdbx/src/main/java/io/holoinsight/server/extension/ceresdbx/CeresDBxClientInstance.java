/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.extension.ceresdbx;

import io.ceresdb.CeresDBClient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jsy1001de
 * @version 1.0: CeresDBxClientInstance.java, Date: 2024-04-18 Time: 15:13
 */
@AllArgsConstructor
@Data
class CeresDBxClientInstance {
  private final String configKey;
  private final CeresDBClient ceresDBClient;
}

