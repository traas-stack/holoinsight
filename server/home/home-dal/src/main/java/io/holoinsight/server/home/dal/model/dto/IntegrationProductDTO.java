/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : IntegrationProductDTO.java, v 0.1 2022年06月08日 16:41 xiangwanpeng Exp $
 */
@Data
public class IntegrationProductDTO {

  public Long id;

  public String name;

  public String profile;

  public String overview;

  public String configuration;

  public Map<String, Object> template;

  public IntegrationMetricsDTO metrics;

  public Boolean status;

  public String type;

  public IntegrationFormDTO form;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String version;
}
