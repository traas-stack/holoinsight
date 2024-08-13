/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.stat.StringsKey;

/**
 * <p>
 * created at 2020-08-21
 *
 * @author xiangfeng.xzc
 */
class PrintItem {
  final StringsKey prefix;
  final StringsKey key;
  final long[] values;
  final double[] values2;

  PrintItem(StringsKey prefix, StringsKey key, long[] values) {
    this.prefix = prefix;
    this.key = key;
    this.values = values;
    this.values2 = null;
  }

  PrintItem(StringsKey prefix, StringsKey key, double[] values2) {
    this.prefix = prefix;
    this.key = key;
    this.values = null;
    this.values2 = values2;
  }
}
