/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaConfigDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaConfigDOMapper {
  long countByExample(GaeaConfigDOExample example);

  int deleteByExample(GaeaConfigDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaConfigDO record);

  int insertSelective(@Param("record") GaeaConfigDO record,
      @Param("selective") GaeaConfigDO.Column... selective);

  GaeaConfigDO selectOneByExample(GaeaConfigDOExample example);

  GaeaConfigDO selectOneByExampleSelective(@Param("example") GaeaConfigDOExample example,
      @Param("selective") GaeaConfigDO.Column... selective);

  GaeaConfigDO selectOneByExampleWithBLOBs(GaeaConfigDOExample example);

  List<GaeaConfigDO> selectByExampleSelective(@Param("example") GaeaConfigDOExample example,
      @Param("selective") GaeaConfigDO.Column... selective);

  List<GaeaConfigDO> selectByExampleWithBLOBs(GaeaConfigDOExample example);

  List<GaeaConfigDO> selectByExample(GaeaConfigDOExample example);

  GaeaConfigDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaConfigDO.Column... selective);

  GaeaConfigDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaConfigDO record,
      @Param("example") GaeaConfigDOExample example,
      @Param("selective") GaeaConfigDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") GaeaConfigDO record,
      @Param("example") GaeaConfigDOExample example);

  int updateByExample(@Param("record") GaeaConfigDO record,
      @Param("example") GaeaConfigDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaConfigDO record,
      @Param("selective") GaeaConfigDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(GaeaConfigDO record);

  int updateByPrimaryKey(GaeaConfigDO record);

  int batchInsert(@Param("list") List<GaeaConfigDO> list);

  int batchInsertSelective(@Param("list") List<GaeaConfigDO> list,
      @Param("selective") GaeaConfigDO.Column... selective);

  int upsert(GaeaConfigDO record);

  int upsertSelective(@Param("record") GaeaConfigDO record,
      @Param("selective") GaeaConfigDO.Column... selective);

  int upsertWithBLOBs(GaeaConfigDO record);
}
