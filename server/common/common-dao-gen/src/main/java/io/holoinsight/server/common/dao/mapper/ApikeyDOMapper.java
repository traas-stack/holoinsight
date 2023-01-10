/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.ApikeyDO;
import io.holoinsight.server.common.dao.entity.ApikeyDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApikeyDOMapper {
  long countByExample(ApikeyDOExample example);

  int deleteByExample(ApikeyDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ApikeyDO record);

  int insertSelective(@Param("record") ApikeyDO record,
      @Param("selective") ApikeyDO.Column... selective);

  ApikeyDO selectOneByExample(ApikeyDOExample example);

  ApikeyDO selectOneByExampleSelective(@Param("example") ApikeyDOExample example,
      @Param("selective") ApikeyDO.Column... selective);

  ApikeyDO selectOneByExampleWithBLOBs(ApikeyDOExample example);

  List<ApikeyDO> selectByExampleSelective(@Param("example") ApikeyDOExample example,
      @Param("selective") ApikeyDO.Column... selective);

  List<ApikeyDO> selectByExampleWithBLOBs(ApikeyDOExample example);

  List<ApikeyDO> selectByExample(ApikeyDOExample example);

  ApikeyDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") ApikeyDO.Column... selective);

  ApikeyDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") ApikeyDO record,
      @Param("example") ApikeyDOExample example, @Param("selective") ApikeyDO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") ApikeyDO record,
      @Param("example") ApikeyDOExample example);

  int updateByExample(@Param("record") ApikeyDO record, @Param("example") ApikeyDOExample example);

  int updateByPrimaryKeySelective(@Param("record") ApikeyDO record,
      @Param("selective") ApikeyDO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(ApikeyDO record);

  int updateByPrimaryKey(ApikeyDO record);

  int batchInsert(@Param("list") List<ApikeyDO> list);

  int batchInsertSelective(@Param("list") List<ApikeyDO> list,
      @Param("selective") ApikeyDO.Column... selective);

  int upsert(ApikeyDO record);

  int upsertSelective(@Param("record") ApikeyDO record,
      @Param("selective") ApikeyDO.Column... selective);

  int upsertWithBLOBs(ApikeyDO record);
}
