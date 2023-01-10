/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.GaeaAgentDO;
import io.holoinsight.server.common.dao.entity.GaeaAgentDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GaeaAgentDOMapper {
  long countByExample(GaeaAgentDOExample example);

  int deleteByExample(GaeaAgentDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(GaeaAgentDO record);

  int insertSelective(@Param("record") GaeaAgentDO record,
      @Param("selective") GaeaAgentDO.Column... selective);

  GaeaAgentDO selectOneByExample(GaeaAgentDOExample example);

  GaeaAgentDO selectOneByExampleSelective(@Param("example") GaeaAgentDOExample example,
      @Param("selective") GaeaAgentDO.Column... selective);

  GaeaAgentDO selectOneByExampleWithBLOBs(GaeaAgentDOExample example);

  List<GaeaAgentDO> selectByExampleSelective(@Param("example") GaeaAgentDOExample example,
      @Param("selective") GaeaAgentDO.Column... selective);

  List<GaeaAgentDO> selectByExampleWithBLOBs(GaeaAgentDOExample example);

  List<GaeaAgentDO> selectByExample(GaeaAgentDOExample example);

  GaeaAgentDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") GaeaAgentDO.Column... selective);

  GaeaAgentDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") GaeaAgentDO record,
      @Param("example") GaeaAgentDOExample example,
      @Param("selective") GaeaAgentDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") GaeaAgentDO record,
      @Param("example") GaeaAgentDOExample example);

  int updateByExample(@Param("record") GaeaAgentDO record,
      @Param("example") GaeaAgentDOExample example);

  int updateByPrimaryKeySelective(@Param("record") GaeaAgentDO record,
      @Param("selective") GaeaAgentDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(GaeaAgentDO record);

  int updateByPrimaryKey(GaeaAgentDO record);

  int batchInsert(@Param("list") List<GaeaAgentDO> list);

  int batchInsertSelective(@Param("list") List<GaeaAgentDO> list,
      @Param("selective") GaeaAgentDO.Column... selective);

  int upsert(GaeaAgentDO record);

  int upsertSelective(@Param("record") GaeaAgentDO record,
      @Param("selective") GaeaAgentDO.Column... selective);

  int upsertWithBLOBs(GaeaAgentDO record);
}
