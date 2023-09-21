/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xiangwanpeng
 * @version : IntegrationProductDTO.java, v 0.1 2022年06月08日 16:41 xiangwanpeng Exp $
 */
@Data
public class IntegrationFormDTO {
  private String formType;
  private List<IntegrationFormInfoDTO> formList;
  private List<String> filters;

  private List<IntegrationConfig> configList;
  private Object extraConfig;

}
