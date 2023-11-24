/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.dal.service.mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.holoinsight.server.meta.dal.service.model.MetaDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ljw
 * @Description MetaDataMapper
 * @date 2023/4/24
 */
@Mapper
@Repository
public interface MetaDataMapper {

  void batchInsertOrUpdate(List<MetaDataDO> metaDataList);

  List<MetaDataDO> selectByUks(@Param("tableName") String tableName,
      @Param("pkList") Collection<String> pkList);

  Integer softDeleteByUks(@Param("tableName") String tableName,
      @Param("pkList") Collection<String> pkList, @Param("gmtModified") Date gmtModified);

  void updateByUk(@Param("tableName") String tableName, @Param("item") MetaDataDO item);

  List<MetaDataDO> queryChangedMeta(@Param("start") Date start, @Param("end") Date end,
      @Param("containDeleted") Boolean containDeleted, @Param("offset") int offset,
      @Param("limit") int limit);

  List<MetaDataDO> queryTableChangedMeta(@Param("table") String table, @Param("start") Date start,
      @Param("end") Date end, @Param("containDeleted") Boolean containDeleted,
      @Param("offset") int offset, @Param("limit") int limit);

  Integer cleanMetaData(@Param("end") Date end);
}
