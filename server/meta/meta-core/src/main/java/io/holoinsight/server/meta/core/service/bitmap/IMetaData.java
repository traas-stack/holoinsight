/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import java.util.Collection;
import java.util.List;

public interface IMetaData {

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
  Collection<MetaDataRow> allRows();

  /**
   * 获取所有维度数据列
   *
   * @return
   */
  Collection<MetaColData> allCols();

  /**
   * 增量合并
   *
   * @param changeLogs
   */
  void merge(long version, List<MetaDataRow> changeLogs);

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
  MetaDataRow getByUk(Object pk);

  /**
   * 根据Id查询
   *
   * @param id
   * @return
   */
  MetaDataRow getById(long id);

  /**
   * 根据主键批量查询
   *
   * @param pks
   * @return
   */
  List<MetaDataRow> getByPks(List<String> pks);

  /**
   * 根据id批量查询
   *
   * @param ids
   * @return
   */
  List<MetaDataRow> getByIds(List<Long> ids);

}
