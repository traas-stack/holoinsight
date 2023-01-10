/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaMasterDO;
import io.holoinsight.server.common.dao.entity.GaeaMasterDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaMasterDOMapper {
  long countByExample(GaeaMasterDOExample example);

  int deleteByExample(GaeaMasterDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaMasterDO record);

  int insertSelective(@Param("record") GaeaMasterDO record,
      @Param("selective") GaeaMasterDO.Column... selective);

  GaeaMasterDO selectOneByExample(GaeaMasterDOExample example);

  GaeaMasterDO selectOneByExampleSelective(@Param("example") GaeaMasterDOExample example,
      @Param("selective") GaeaMasterDO.Column... selective);

  List<GaeaMasterDO> selectByExampleSelective(@Param("example") GaeaMasterDOExample example,
      @Param("selective") GaeaMasterDO.Column... selective);

  List<GaeaMasterDO> selectByExample(GaeaMasterDOExample example);

  GaeaMasterDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaMasterDO.Column... selective);

  GaeaMasterDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaMasterDO record,
      @Param("example") GaeaMasterDOExample example,
      @Param("selective") GaeaMasterDO.Column... selective);

  int updateByExample(@Param("record") GaeaMasterDO record,
      @Param("example") GaeaMasterDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaMasterDO record,
      @Param("selective") GaeaMasterDO.Column... selective);

  int updateByPrimaryKey(GaeaMasterDO record);

  int batchInsert(@Param("list") List<GaeaMasterDO> list);

  int batchInsertSelective(@Param("list") List<GaeaMasterDO> list,
      @Param("selective") GaeaMasterDO.Column... selective);

  int upsert(GaeaMasterDO record);

  int upsertSelective(@Param("record") GaeaMasterDO record,
      @Param("selective") GaeaMasterDO.Column... selective);
}
