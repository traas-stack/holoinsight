/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaLockDO;
import io.holoinsight.server.common.dao.entity.GaeaLockDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaLockDOMapper {
  long countByExample(GaeaLockDOExample example);

  int deleteByExample(GaeaLockDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaLockDO record);

  int insertSelective(@Param("record") GaeaLockDO record,
      @Param("selective") GaeaLockDO.Column... selective);

  GaeaLockDO selectOneByExample(GaeaLockDOExample example);

  GaeaLockDO selectOneByExampleSelective(@Param("example") GaeaLockDOExample example,
      @Param("selective") GaeaLockDO.Column... selective);

  List<GaeaLockDO> selectByExampleSelective(@Param("example") GaeaLockDOExample example,
      @Param("selective") GaeaLockDO.Column... selective);

  List<GaeaLockDO> selectByExample(GaeaLockDOExample example);

  GaeaLockDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaLockDO.Column... selective);

  GaeaLockDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaLockDO record,
      @Param("example") GaeaLockDOExample example,
      @Param("selective") GaeaLockDO.Column... selective);

  int updateByExample(@Param("record") GaeaLockDO record,
      @Param("example") GaeaLockDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaLockDO record,
      @Param("selective") GaeaLockDO.Column... selective);

  int updateByPrimaryKey(GaeaLockDO record);

  int batchInsert(@Param("list") List<GaeaLockDO> list);

  int batchInsertSelective(@Param("list") List<GaeaLockDO> list,
      @Param("selective") GaeaLockDO.Column... selective);

  int upsert(GaeaLockDO record);

  int upsertSelective(@Param("record") GaeaLockDO record,
      @Param("selective") GaeaLockDO.Column... selective);
}
