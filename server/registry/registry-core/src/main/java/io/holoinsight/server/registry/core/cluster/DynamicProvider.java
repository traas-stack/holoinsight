/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import io.grpc.stub.AbstractStub;

/**
 * <p>
 * created at 2022/4/17
 *
 * @author zzhb101
 */
public interface DynamicProvider {
  <S extends AbstractStub<S>> S customize(S stub);
}
