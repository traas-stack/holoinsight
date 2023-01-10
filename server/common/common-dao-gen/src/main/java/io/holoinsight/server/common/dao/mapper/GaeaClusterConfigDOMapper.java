/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaClusterConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaClusterConfigDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaClusterConfigDOMapper {
  long countByExample(GaeaClusterConfigDOExample example);

  int deleteByExample(GaeaClusterConfigDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaClusterConfigDO record);

  int insertSelective(@Param("record") GaeaClusterConfigDO record,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  GaeaClusterConfigDO selectOneByExample(GaeaClusterConfigDOExample example);

  GaeaClusterConfigDO selectOneByExampleSelective(
      @Param("example") GaeaClusterConfigDOExample example,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  List<GaeaClusterConfigDO> selectByExampleSelective(
      @Param("example") GaeaClusterConfigDOExample example,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  List<GaeaClusterConfigDO> selectByExample(GaeaClusterConfigDOExample example);

  GaeaClusterConfigDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  GaeaClusterConfigDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaClusterConfigDO record,
      @Param("example") GaeaClusterConfigDOExample example,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  int updateByExample(@Param("record") GaeaClusterConfigDO record,
      @Param("example") GaeaClusterConfigDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaClusterConfigDO record,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  int updateByPrimaryKey(GaeaClusterConfigDO record);

  int batchInsert(@Param("list") List<GaeaClusterConfigDO> list);

  int batchInsertSelective(@Param("list") List<GaeaClusterConfigDO> list,
      @Param("selective") GaeaClusterConfigDO.Column... selective);

  int upsert(GaeaClusterConfigDO record);

  int upsertSelective(@Param("record") GaeaClusterConfigDO record,
      @Param("selective") GaeaClusterConfigDO.Column... selective);
}
