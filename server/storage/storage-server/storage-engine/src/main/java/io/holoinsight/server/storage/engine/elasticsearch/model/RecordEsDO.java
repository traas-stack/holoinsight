/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.model;

import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jiwliu
 * @version : RecordDO.java, v 0.1 2022年10月11日 15:17 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class RecordEsDO implements Serializable {

  private static final long serialVersionUID = -2175475836786269673L;

  public static final String TIME_BUCKET = "time_bucket";

  @Column(name = TIME_BUCKET)
  private long timeBucket;

  public abstract String indexName();
}
