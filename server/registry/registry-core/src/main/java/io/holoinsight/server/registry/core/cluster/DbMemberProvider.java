/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import io.holoinsight.server.common.dao.mapper.GaeaClusterConfigDOMapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * <p>
 * created at 2022/3/14
 *
 * @author zzhb101
 */
public class DbMemberProvider implements MemberProvider {
  @Autowired
  private GaeaClusterConfigDOMapper mapper;

  @Override
  public Set<Endpoint> members() {
    // mapper.selectByExample("")
    return null;
  }
}
