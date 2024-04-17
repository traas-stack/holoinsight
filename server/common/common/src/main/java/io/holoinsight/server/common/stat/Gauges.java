/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.stat.StringsKey;
import lombok.Data;

import java.util.function.Function;

/**
 * @author masaimu
 * @version 2024-04-24 11:23:00
 */
@Data
public class Gauges<T> {
  final StringsKey key;
  final T state;
  final Function<T, long[]> provider;
}
