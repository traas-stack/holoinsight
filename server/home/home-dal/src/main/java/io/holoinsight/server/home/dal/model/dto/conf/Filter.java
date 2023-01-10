/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: Filter.java, v 0.1 2022年03月14日 8:20 下午 jinsong.yjs Exp $
 */
@Data
public class Filter implements Serializable {
  private static final long serialVersionUID = -3272585845278117842L;

  // 简易过滤，contains
  // 高级过滤，leftRight
  public String type;

  public Rule rule;

  public List<String> values;

}
