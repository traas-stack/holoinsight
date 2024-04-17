/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
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
