/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: TokenQueryRequest.java, v 0.1 2022年06月14日 2:13 下午 jinsong.yjs Exp $
 */
@Data
public class TokenQueryRequest {
  /**
   * 系统名称
   */
  public String accessId;
  /**
   * 系统apiKey
   */
  public String accessKey;
}
