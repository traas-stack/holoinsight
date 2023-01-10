/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: FavRequest.java, v 0.1 2022年05月30日 2:53 下午 jinsong.yjs Exp $
 */
@Data
public class FavRequestCmd {
  private List<String> relateIds;
  private String type;
}
