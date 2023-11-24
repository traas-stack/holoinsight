/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.dal.service.model;

import java.util.Date;

import lombok.Data;

/**
 * @author ljw
 * @Description meta data
 * @date 2023/4/24
 */
@Data
public class MetaDataDO {

  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String tableName;

  private String uk;

  private String json;

  private String annotations;

  private Integer deleted;

}
