/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogSample implements Serializable {

  private static final long serialVersionUID = -7744949698357759207L;

  private String hostname;
  private List<String[]> logs;

  public int size() {
    return CollectionUtils.size(this.logs);
  }

}
