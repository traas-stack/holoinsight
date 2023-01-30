/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.springboot;

import io.holoinsight.server.registry.model.integration.LocalIntegrationTask;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzhb101
 * @version : JvmTask.java, v 0.1 2022年06月29日 14:03 xiangwanpeng Exp $
 */
@Data
@NoArgsConstructor
public class SpringBootTask extends LocalIntegrationTask {
  private SpringBootConf conf;
}
