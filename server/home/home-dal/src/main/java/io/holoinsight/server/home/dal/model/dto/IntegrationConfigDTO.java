/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

/**
 * @author jsy1001de
 * @version 1.0: IntegrationConfigDTO.java, Date: 2023-08-30 Time: 10:20
 */
@Data
public class IntegrationConfigDTO {
  private Boolean useInApp;
  private Boolean canCustom;
}
