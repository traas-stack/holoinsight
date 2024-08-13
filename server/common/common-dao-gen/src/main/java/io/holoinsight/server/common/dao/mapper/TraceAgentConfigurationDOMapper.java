/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.TraceAgentConfigurationDO;
import io.holoinsight.server.common.dao.entity.TraceAgentConfigurationDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TraceAgentConfigurationDOMapper {
  long countByExample(TraceAgentConfigurationDOExample example);

  int deleteByExample(TraceAgentConfigurationDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(TraceAgentConfigurationDO record);

  int insertSelective(@Param("record") TraceAgentConfigurationDO record,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  TraceAgentConfigurationDO selectOneByExample(TraceAgentConfigurationDOExample example);

  TraceAgentConfigurationDO selectOneByExampleSelective(
      @Param("example") TraceAgentConfigurationDOExample example,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  TraceAgentConfigurationDO selectOneByExampleWithBLOBs(TraceAgentConfigurationDOExample example);

  List<TraceAgentConfigurationDO> selectByExampleSelective(
      @Param("example") TraceAgentConfigurationDOExample example,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  List<TraceAgentConfigurationDO> selectByExampleWithBLOBs(
      TraceAgentConfigurationDOExample example);

  List<TraceAgentConfigurationDO> selectByExample(TraceAgentConfigurationDOExample example);

  TraceAgentConfigurationDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  TraceAgentConfigurationDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") TraceAgentConfigurationDO record,
      @Param("example") TraceAgentConfigurationDOExample example,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") TraceAgentConfigurationDO record,
      @Param("example") TraceAgentConfigurationDOExample example);

  int updateByExample(@Param("record") TraceAgentConfigurationDO record,
      @Param("example") TraceAgentConfigurationDOExample example);

  int updateByPrimaryKeySelective(@Param("record") TraceAgentConfigurationDO record,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(TraceAgentConfigurationDO record);

  int updateByPrimaryKey(TraceAgentConfigurationDO record);

  int batchInsert(@Param("list") List<TraceAgentConfigurationDO> list);

  int batchInsertSelective(@Param("list") List<TraceAgentConfigurationDO> list,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  int upsert(TraceAgentConfigurationDO record);

  int upsertSelective(@Param("record") TraceAgentConfigurationDO record,
      @Param("selective") TraceAgentConfigurationDO.Column... selective);

  int upsertWithBLOBs(TraceAgentConfigurationDO record);
}
