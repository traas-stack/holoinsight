/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: IntegrationGeneratedDTO.java, v 0.1 2022年12月14日 上午11:43 jinsong.yjs Exp $
 */
@Data
public class IntegrationGeneratedDTO {
  public Long id;

  public String tenant;
  public String workspace;

  // appName or other
  public String name;

  // jvm,system,checkservice
  public String item;

  // integration, mysql, apm
  public String product;


  // detail config
  public Map<String, Object> config;

  // is deleted 0 or 1
  public boolean deleted;

  // is use custom
  public boolean custom;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
