/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.stat.StringsKey;
import lombok.Data;

import java.util.function.Function;

/**
 * @author masaimu
 * @version 2024-04-24 11:24:00
 */
@Data
public class Gauge<T> {
  final StringsKey key;
  final T state;
  final Function<? super T, ? extends Number> provider;
}
