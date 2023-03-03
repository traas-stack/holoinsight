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
 * @version : Pagination.java, v 0.1 2022年09月20日 15:38 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination implements Serializable {
  private static final long serialVersionUID = 3817409052867396035L;
  private int pageNum;
  private int pageSize;
}
