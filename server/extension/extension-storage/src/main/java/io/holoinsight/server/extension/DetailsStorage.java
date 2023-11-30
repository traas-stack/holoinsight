/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension;

import io.holoinsight.server.extension.model.Table;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
public interface DetailsStorage {
  Mono<Void> write(String tenant, Table table);
}
