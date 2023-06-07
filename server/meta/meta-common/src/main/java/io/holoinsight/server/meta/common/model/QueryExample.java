/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: QueryExample.java, v 0.1 2022年03月08日 11:07 上午 jinsong.yjs Exp $
 */
@Data
public class QueryExample implements Serializable {

  private Map<String, Object> params = new HashMap<>();

  private List<String> rowKeys = new ArrayList<>();
}
