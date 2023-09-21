/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuConfig.java, v 0.1 2022年12月06日 上午10:49 jinsong.yjs Exp $
 */
@Data
public class DisplayMenuConfig {
  private String name;
  private String enName;
  private String url;
  private String type;
  private String templateId;
  private String showItem;
  private List<DisplayMenuConfig> children;
}
