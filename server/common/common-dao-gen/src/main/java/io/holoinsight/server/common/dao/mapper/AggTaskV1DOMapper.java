/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.AggTaskV1DO;
import io.holoinsight.server.common.dao.entity.AggTaskV1DOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AggTaskV1DOMapper {
  long countByExample(AggTaskV1DOExample example);

  int deleteByExample(AggTaskV1DOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(AggTaskV1DO record);

  int insertSelective(@Param("record") AggTaskV1DO record,
      @Param("selective") AggTaskV1DO.Column... selective);

  AggTaskV1DO selectOneByExample(AggTaskV1DOExample example);

  AggTaskV1DO selectOneByExampleSelective(@Param("example") AggTaskV1DOExample example,
      @Param("selective") AggTaskV1DO.Column... selective);

  AggTaskV1DO selectOneByExampleWithBLOBs(AggTaskV1DOExample example);

  List<AggTaskV1DO> selectByExampleSelective(@Param("example") AggTaskV1DOExample example,
      @Param("selective") AggTaskV1DO.Column... selective);

  List<AggTaskV1DO> selectByExampleWithBLOBs(AggTaskV1DOExample example);

  List<AggTaskV1DO> selectByExample(AggTaskV1DOExample example);

  AggTaskV1DO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") AggTaskV1DO.Column... selective);

  AggTaskV1DO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") AggTaskV1DO record,
      @Param("example") AggTaskV1DOExample example,
      @Param("selective") AggTaskV1DO.Column... selective);

  int updateByExampleWithBLOBs(@Param("record") AggTaskV1DO record,
      @Param("example") AggTaskV1DOExample example);

  int updateByExample(@Param("record") AggTaskV1DO record,
      @Param("example") AggTaskV1DOExample example);

  int updateByPrimaryKeySelective(@Param("record") AggTaskV1DO record,
      @Param("selective") AggTaskV1DO.Column... selective);

  int updateByPrimaryKeyWithBLOBs(AggTaskV1DO record);

  int updateByPrimaryKey(AggTaskV1DO record);

  int batchInsert(@Param("list") List<AggTaskV1DO> list);

  int batchInsertSelective(@Param("list") List<AggTaskV1DO> list,
      @Param("selective") AggTaskV1DO.Column... selective);

  int upsert(AggTaskV1DO record);

  int upsertSelective(@Param("record") AggTaskV1DO record,
      @Param("selective") AggTaskV1DO.Column... selective);

  int upsertWithBLOBs(AggTaskV1DO record);
}
