/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.TraceAgentConfPropDO;
import io.holoinsight.server.common.dao.entity.TraceAgentConfPropDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TraceAgentConfPropDOMapper {
  long countByExample(TraceAgentConfPropDOExample example);

  int deleteByExample(TraceAgentConfPropDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(TraceAgentConfPropDO record);

  int insertSelective(@Param("record") TraceAgentConfPropDO record,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  TraceAgentConfPropDO selectOneByExample(TraceAgentConfPropDOExample example);

  TraceAgentConfPropDO selectOneByExampleSelective(
      @Param("example") TraceAgentConfPropDOExample example,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  TraceAgentConfPropDO selectOneByExampleWithBLOBs(TraceAgentConfPropDOExample example);

  List<TraceAgentConfPropDO> selectByExampleSelective(
      @Param("example") TraceAgentConfPropDOExample example,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  List<TraceAgentConfPropDO> selectByExampleWithBLOBs(TraceAgentConfPropDOExample example);

  List<TraceAgentConfPropDO> selectByExample(TraceAgentConfPropDOExample example);

  TraceAgentConfPropDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  TraceAgentConfPropDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") TraceAgentConfPropDO record,
      @Param("example") TraceAgentConfPropDOExample example,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") TraceAgentConfPropDO record,
      @Param("example") TraceAgentConfPropDOExample example);

  int updateByExample(@Param("record") TraceAgentConfPropDO record,
      @Param("example") TraceAgentConfPropDOExample example);

  int updateByPrimaryKeySelective(@Param("record") TraceAgentConfPropDO record,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(TraceAgentConfPropDO record);

  int updateByPrimaryKey(TraceAgentConfPropDO record);

  int batchInsert(@Param("list") List<TraceAgentConfPropDO> list);

  int batchInsertSelective(@Param("list") List<TraceAgentConfPropDO> list,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  int upsert(TraceAgentConfPropDO record);

  int upsertSelective(@Param("record") TraceAgentConfPropDO record,
      @Param("selective") TraceAgentConfPropDO.Column... selective);

  int upsertWithBLOBs(TraceAgentConfPropDO record);
}
