/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jiwliu
 * @version : Duration.java, v 0.1 2022年09月20日 15:37 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Duration implements Serializable {

  private static final long serialVersionUID = 7983574358510487461L;

  private long start;
  private long end;
  private String step;

}
