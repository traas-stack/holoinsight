/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.MetaDimData;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: MetaDimDataService.java, Date: 2024-04-18 Time: 11:57
 */
public interface MetaDimDataService extends IService<MetaDimData> {

  void batchInsertOrUpdate(String tableName, List<MetaDimData> metaDimDatas);

  List<MetaDimData> selectByUks(String tableName, Collection<String> pkList);

  Integer softDeleteByUks(String tableName, Collection<String> pkList, Date gmtModified);

  void updateByUk(String tableName, MetaDimData item);

  List<MetaDimData> queryChangedMeta(Date start, Date end, Boolean containDeleted, int pateNum,
      int pageSize);

  List<MetaDimData> queryTableChangedMeta(String table, Date start, Date end,
      Boolean containDeleted, int pateNum, int pageSize);

  // Integer cleanMetaData(Date end);
}
