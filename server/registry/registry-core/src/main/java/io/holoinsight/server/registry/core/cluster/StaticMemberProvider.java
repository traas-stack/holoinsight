/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import java.util.Collections;
import java.util.Set;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
public class StaticMemberProvider implements MemberProvider {
  @Override
  public Set<Endpoint> members() {
    return Collections.singleton(new Endpoint("127.0.0.1", Utils.DEFAULT_PORT));
  }
}
