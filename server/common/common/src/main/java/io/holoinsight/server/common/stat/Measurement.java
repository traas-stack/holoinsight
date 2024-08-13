/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.stat.StringsKey;
import lombok.Data;

/**
 * <p>
 * created at 2020-08-27
 *
 * @author xiangfeng.xzc
 */
@Data
public final class Measurement {
  final StringsKey key;
  final long[] values;
}
