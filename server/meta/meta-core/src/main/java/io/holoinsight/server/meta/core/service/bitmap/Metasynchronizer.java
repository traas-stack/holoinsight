/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import java.util.List;

/**
 *
 * @author wanpeng.xwp
 * @version : Metasynchronizer.java, v 0.1 2023年11月23日 19:25 wanpeng.xwp Exp $
 */
public interface Metasynchronizer {
  List<DimDataRow> sync(String tableName, long start, long end);
}
