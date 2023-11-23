/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import java.util.Collection;
import java.util.List;

public interface DimData {

  /**
   * 维表名称
   *
   * @return
   */
  String getTableName();


  /**
   * 获取所有维度数据行
   *
   * @return
   */
  Collection<DimDataRow> allRows();

  /**
   * 获取所有维度数据列
   *
   * @return
   */
  Collection<DimColData> allCols();

  /**
   * 增量合并
   *
   * @param changeLogs
   */
  void merge(long version, List<DimDataRow> changeLogs);

  /**
   * 是否过期
   *
   * @return
   */
  boolean isExpired();

  /**
   * 根据主键查询
   *
   * @param pk
   * @return
   */
  DimDataRow getByUk(Object pk);

  /**
   * 根据Id查询
   *
   * @param id
   * @return
   */
  DimDataRow getById(long id);

  /**
   * 根据主键批量查询
   *
   * @param pks
   * @return
   */
  List<DimDataRow> getByPks(List<String> pks);

  /**
   * 根据id批量查询
   *
   * @param ids
   * @return
   */
  List<DimDataRow> getByIds(List<Long> ids);

}
