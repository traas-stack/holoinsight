/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: Token.java, v 0.1 2022年05月19日 5:48 下午 jinsong.yjs Exp $
 */
@Data
public class TokenDTO {
  private Long id;

  private String name;

  private String token;

  private Long queryLimit;

  private Long tenantId;

  private Long workspaceId;

  private Integer intervalCount;

  private Integer queueMaxSize;

  private Integer concurent;

  private Boolean share;

  private String authTenant;

  private String authBizDomain;

  private String authWorkspace;

  private Integer modelingQueueMaxSize;

  private String config;

}
