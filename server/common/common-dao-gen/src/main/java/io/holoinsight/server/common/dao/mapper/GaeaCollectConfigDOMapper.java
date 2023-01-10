/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaCollectConfigDOMapper {
  long countByExample(GaeaCollectConfigDOExample example);

  int deleteByExample(GaeaCollectConfigDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaCollectConfigDO record);

  int insertSelective(@Param("record") GaeaCollectConfigDO record,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  GaeaCollectConfigDO selectOneByExample(GaeaCollectConfigDOExample example);

  GaeaCollectConfigDO selectOneByExampleSelective(
      @Param("example") GaeaCollectConfigDOExample example,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  GaeaCollectConfigDO selectOneByExampleWithBLOBs(GaeaCollectConfigDOExample example);

  List<GaeaCollectConfigDO> selectByExampleSelective(
      @Param("example") GaeaCollectConfigDOExample example,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  List<GaeaCollectConfigDO> selectByExampleWithBLOBs(GaeaCollectConfigDOExample example);

  List<GaeaCollectConfigDO> selectByExample(GaeaCollectConfigDOExample example);

  GaeaCollectConfigDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  GaeaCollectConfigDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaCollectConfigDO record,
      @Param("example") GaeaCollectConfigDOExample example,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") GaeaCollectConfigDO record,
      @Param("example") GaeaCollectConfigDOExample example);

  int updateByExample(@Param("record") GaeaCollectConfigDO record,
      @Param("example") GaeaCollectConfigDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaCollectConfigDO record,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(GaeaCollectConfigDO record);

  int updateByPrimaryKey(GaeaCollectConfigDO record);

  int batchInsert(@Param("list") List<GaeaCollectConfigDO> list);

  int batchInsertSelective(@Param("list") List<GaeaCollectConfigDO> list,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  int upsert(GaeaCollectConfigDO record);

  int upsertSelective(@Param("record") GaeaCollectConfigDO record,
      @Param("selective") GaeaCollectConfigDO.Column... selective);

  int upsertWithBLOBs(GaeaCollectConfigDO record);
}
